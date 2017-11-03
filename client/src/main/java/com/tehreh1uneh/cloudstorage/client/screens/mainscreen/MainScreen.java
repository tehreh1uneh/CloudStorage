package com.tehreh1uneh.cloudstorage.client.screens.mainscreen;

import com.tehreh1uneh.cloudstorage.client.screens.BaseScreen;
import com.tehreh1uneh.cloudstorage.common.messages.files.*;
import com.tehreh1uneh.cloudstorage.common.notification.Notifier;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.stage.FileChooser;
import org.apache.log4j.Logger;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.tehreh1uneh.cloudstorage.client.screenmanager.Config.*;

public final class MainScreen extends BaseScreen implements Initializable {

    private static final Logger logger = Logger.getLogger(MainScreen.class);

    //region View fields
    @FXML
    TableColumn<TableRowData, String> colType;
    @FXML
    TableColumn<TableRowData, String> colSize;
    private final ObservableList<TableRowData> tableData = FXCollections.observableArrayList();
    @FXML
    private TableView<TableRowData> tableFiles;
    @FXML
    private TableColumn<TableRowData, ImageView> colImage;
    @FXML
    private TableColumn<TableRowData, String> colFileName;
    @FXML
    private TableColumn<TableRowData, String> colModified;
    @FXML
    private Button buttonUpload;
    @FXML
    private Button buttonDownload;
    @FXML
    private Button buttonDelete;
    @FXML
    private Button buttonRename;
    @FXML
    private ProgressIndicator progressIndicator;
    @FXML
    private Label progressLabel;
    //endregion

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colImage.setCellValueFactory(new PropertyValueFactory<>("icon"));
        colImage.setPrefWidth(COLUMN_FOLDER_WIDTH);

        colFileName.setCellValueFactory(new PropertyValueFactory<>("fileName"));
        colModified.setCellValueFactory(new PropertyValueFactory<>("modified"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colSize.setCellValueFactory(new PropertyValueFactory<>("size"));

        logger.info("Инициалищация основного экрана успешно завершена");
    }

    //region Events

    @FXML
    private void onActionSourceCode() {
        logger.info("Попытка перехода по ссылке на исходный код");
        String url = "https://github.com/tehreh1uneh/CloudStorage";
        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            try {
                desktop.browse(new URI(url));
                logger.info("Переход по ссылке исходноо кода осуществлен успешно");
            } catch (IOException | URISyntaxException e) {
                logger.warn("Не удалось перейти по ссылке на исходный код", e);
            }
        }
    }

    @FXML
    private void onActionExit() {
        System.exit(0);
    }

    @FXML
    private void onTableMouseClick(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() > 1) {
            handleDoubleClick();
        }
    }

    @FXML
    private void onTableKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            handleDoubleClick();
        } else if (keyEvent.getCode() == KeyCode.DELETE) {
            deleteFile();
        } else if (keyEvent.getCode() == KeyCode.INSERT) {
            chooseAndSendFiles();
        } else if (keyEvent.getCode() == KeyCode.F2) {
            renameFile();
        }
    }

    @FXML
    private void onActionLogOut() {
        logger.info("Попытка разлогиниться");
        clientApp.logOut();
    }

    @FXML
    private void onActionUpload() {
        chooseAndSendFiles();
    }

    @FXML
    private void onActionButtonDownload() {
        blockButton(buttonDownload);
        setProgressIndicatorActivity(true, "Загрузка файла...");
        new Thread(this::downloadFile).start();
    }

    @FXML
    private void onActionButtonDelete() {
        blockButton(buttonDelete);
        setProgressIndicatorActivity(true, "Удаление файла...");
        new Thread(this::deleteFile).start();
    }

    @FXML
    private void onActionButtonRename() {
        renameFile();
    }

    public void onDragOver(DragEvent dragEvent) {
        Dragboard board = dragEvent.getDragboard();
        if (board.hasFiles()) {
            dragEvent.acceptTransferModes(TransferMode.COPY);
        }

    }

    @FXML
    private void onDragDropped(DragEvent dragEvent) {
        Dragboard board = dragEvent.getDragboard();
        List<File> files = board.getFiles();
        for (File file : files) {
            sendFile(file);
        }
    }

    //endregion

    //region Actions
    private void downloadFile() {
        if (!rowExists()) return;
        clientApp.send(new FileRequestMessage(getActiveRowFile()));
    }

    private void deleteFile() {
        if (!rowExists()) return;
        File file = getActiveRowFile();
        if (file == null) {
            unblockAllButtons();
            setProgressIndicatorActivity(false, "");
            return;
        }
        clientApp.send(new FileDeleteMessage(getActiveRowFile()));
    }

    @SuppressWarnings("ALL")
    private void renameFile() {
        if (tableFiles.getSelectionModel().getFocusedIndex() == -1) return;
        blockButton(buttonRename);
        setProgressIndicatorActivity(true, "Переименование файла...");

        String oldName = getActiveRowFileName();

        TextInputDialog dialog = new TextInputDialog(oldName);
        dialog.setTitle("Переименование");
        dialog.setHeaderText("Переименование файла");
        dialog.setContentText("Введите новое имя файла:");

        Pattern pattern = Pattern.compile(
                "# Match a valid Windows filename (unspecified file system).          \n" +
                        "^                                # Anchor to start of string.        \n" +
                        "(?!                              # Assert filename is not: CON, PRN, \n" +
                        "  (?:                            # AUX, NUL, COM1, COM2, COM3, COM4, \n" +
                        "    CON|PRN|AUX|NUL|             # COM5, COM6, COM7, COM8, COM9,     \n" +
                        "    COM[1-9]|LPT[1-9]            # LPT1, LPT2, LPT3, LPT4, LPT5,     \n" +
                        "  )                              # LPT6, LPT7, LPT8, and LPT9...     \n" +
                        "  (?:\\.[^.]*)?                  # followed by optional extension    \n" +
                        "  $                              # and end of string                 \n" +
                        ")                                # End negative lookahead assertion. \n" +
                        "[^<>:\"/\\\\|?*\\x00-\\x1F]*     # Zero or more valid filename chars.\n" +
                        "[^<>:\"/\\\\|?*\\x00-\\x1F\\ .]  # Last char is not a space or dot.  \n" +
                        "$                                # Anchor to end of string.            ",
                Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.COMMENTS);

        Optional<String> result = dialog.showAndWait();

        result.ifPresentOrElse(
                newName -> {
                    Matcher matcher = pattern.matcher(newName);
                    if (!matcher.matches()) {
                        Notifier.show(5d, "Ошибка", "Имя файла содержит недопустимые символы или является зарезервированным", Notifier.NotificationType.ERROR);
                        unblockAllButtons();
                        setProgressIndicatorActivity(false, "");
                        return;
                    }
                    clientApp.send(new FileRenameMessage(getActiveRowFile(), newName));
                },
                () -> {
                    unblockAllButtons();
                    setProgressIndicatorActivity(false, "");
                });
    }

    @SuppressWarnings("ALL")
    public void onActionCreateFolder() {

        TextInputDialog dialog = new TextInputDialog("Новая папка");
        dialog.setTitle("Создать папку");
        dialog.setHeaderText("Создание папки");
        dialog.setContentText("Введите имя папки:");

        Pattern pattern = Pattern.compile(
                "# Match a valid Windows filename (unspecified file system).          \n" +
                        "^                                # Anchor to start of string.        \n" +
                        "(?!                              # Assert filename is not: CON, PRN, \n" +
                        "  (?:                            # AUX, NUL, COM1, COM2, COM3, COM4, \n" +
                        "    CON|PRN|AUX|NUL|             # COM5, COM6, COM7, COM8, COM9,     \n" +
                        "    COM[1-9]|LPT[1-9]            # LPT1, LPT2, LPT3, LPT4, LPT5,     \n" +
                        "  )                              # LPT6, LPT7, LPT8, and LPT9...     \n" +
                        "  (?:\\.[^.]*)?                  # followed by optional extension    \n" +
                        "  $                              # and end of string                 \n" +
                        ")                                # End negative lookahead assertion. \n" +
                        "[^<>:\"/\\\\|?*\\x00-\\x1F]*     # Zero or more valid filename chars.\n" +
                        "[^<>:\"/\\\\|?*\\x00-\\x1F\\ .]  # Last char is not a space or dot.  \n" +
                        "$                                # Anchor to end of string.            ",
                Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.COMMENTS);

        Optional<String> result = dialog.showAndWait();

        result.ifPresent(newName -> {
            Matcher matcher = pattern.matcher(newName);
            if (!matcher.matches()) {
                Notifier.show(5d, "Ошибка", "Имя папки содержит недопустимые символы или является зарезервированным", Notifier.NotificationType.ERROR);
                return;
            }
            clientApp.send(new FolderCreateMessage(newName));
        });
    }

    //endregion

    //region Service

    private synchronized void handleDoubleClick() {
        if (rowExists()) {

            File currentFile = getActiveRowFile();
            if (currentFile == null) {
                clientApp.send(new GoBackMessage());
            } else if (currentFile.isDirectory()) {
                clientApp.send(new FilesListRequest(true, currentFile));
            } else {
                downloadFile();
            }
        }
    }

    private boolean rowExists() {
        return tableFiles.getSelectionModel().getFocusedIndex() != -1;
    }

    private void chooseAndSendFiles() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выберите файлы для передачи");

        List<java.io.File> files = fileChooser.showOpenMultipleDialog(clientApp.getStage());
        if (files != null) {
            for (java.io.File file : files) {
                sendFile(file);
            }
        }
    }

    private void sendFile(File file) {
        if (file.length() <= MAX_FILE_SIZE) {
            clientApp.send(new FileMessage(file));
            blockButton(buttonDownload);
            setProgressIndicatorActivity(true, "Выгрузка файла...");
        } else {
            Notifier.show(5d, "Выгрузка", "Максимальный размер передаваемого файла: " + MAX_FILE_SIZE_DESCRIPTION + ". \n Файл \"" + file.getName() + "\" пропущен", Notifier.NotificationType.WARNING);
        }
    }

    private synchronized File getActiveRowFile() {
        return tableData.get(tableFiles.getSelectionModel().getFocusedIndex()).getFile();
    }

    private String getActiveRowFileName() {
        return getActiveRowFile().getName();
    }

    public synchronized void fillTable(ArrayList<File> files, boolean root) {
        tableData.clear();

        if (!root) {
            tableData.add(new TableRowData());
        }
        for (File file : files) {
            tableData.add(new TableRowData(file));
        }
        sortByIcon();
        tableFiles.setItems(tableData);
    }

    private void sortByIcon() {
        tableData.sort((row, rowNext) -> {
            if (row.getIcon() == null && rowNext.getIcon() != null) {
                return 1;
            } else if (row.getIcon() != null && rowNext.getIcon() == null) {
                return -1;
            } else {
                return 0;
            }
        });
    }

    public synchronized void setProgressIndicatorActivity(boolean visible, String message) {
        Platform.runLater(() -> {
            progressIndicator.setVisible(visible);
            progressLabel.setText(visible ? message : "");
        });
    }

    private void blockButton(Button button) {
        button.setDisable(true);
    }

    // TODO unblock one button, server should send it
    public void unblockAllButtons() {
        buttonDelete.setDisable(false);
        buttonDownload.setDisable(false);
        buttonRename.setDisable(false);
        buttonUpload.setDisable(false);
    }


    //endregion

}



