package com.tehreh1uneh.cloudstorage.client.screens.mainscreen;

import com.tehreh1uneh.cloudstorage.client.screens.BaseScreen;
import com.tehreh1uneh.cloudstorage.common.messages.files.FileDeleteMessage;
import com.tehreh1uneh.cloudstorage.common.messages.files.FileMessage;
import com.tehreh1uneh.cloudstorage.common.messages.files.FileRenameMessage;
import com.tehreh1uneh.cloudstorage.common.messages.files.FileRequestMessage;
import com.tehreh1uneh.cloudstorage.common.notification.Notifier;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
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

import static com.tehreh1uneh.cloudstorage.client.screenmanager.Config.MAX_FILE_SIZE;
import static com.tehreh1uneh.cloudstorage.client.screenmanager.Config.MAX_FILE_SIZE_DESCRIPTION;

public final class MainScreen extends BaseScreen implements Initializable {

    private static final Logger logger = Logger.getLogger(MainScreen.class);
    @FXML
    TableColumn<TableRowData, String> colType;
    @FXML
    TableColumn<TableRowData, String> colSize;
    @FXML
    private TableView tableFiles;
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

    private ObservableList<TableRowData> tableData = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        colFileName.setCellValueFactory(new PropertyValueFactory<>("fileName"));
        colModified.setCellValueFactory(new PropertyValueFactory<>("modified"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colSize.setCellValueFactory(new PropertyValueFactory<>("size"));

        logger.info("Инициалищация основного экрана успешно завершена");
    }

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
            downloadFile();
        }
    }

    @FXML
    private void onTableKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            downloadFile();
        } else if (keyEvent.getCode() == KeyCode.DELETE) {
            deleteFile();
        }
    }

    @FXML
    private void onActionLogOut() {
        logger.info("Попытка разлогиниться");
        clientApp.logOut();
    }

    @FXML
    private void sendFiles() {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выберите файлы для передачи");

        List<java.io.File> files = fileChooser.showOpenMultipleDialog(clientApp.getStage());
        if (files != null) {
            for (java.io.File file : files) {
                if (file.length() <= MAX_FILE_SIZE) {
                    clientApp.send(new FileMessage(file));
                    blockButton(buttonDownload);
                    setProgressIndicatorActivity(true, "Выгрузка файла...");
                } else {
                    Notifier.show(5d, "Выгрузка", "Максимальный размер передаваемого файла: " + MAX_FILE_SIZE_DESCRIPTION + ". \n Файл \"" + file.getName() + "\" пропущен", Notifier.NotificationType.WARNING);
                }
            }
        }
    }

    @FXML
    private void onActionButtonDownload(ActionEvent actionEvent) {
        blockButton(buttonDownload);
        setProgressIndicatorActivity(true, "Загрузка файла...");
        new Thread(this::downloadFile).start();
    }

    @FXML
    private void onActionButtonDelete(ActionEvent actionEvent) {
        blockButton(buttonDelete);
        setProgressIndicatorActivity(true, "Удаление файла...");
        new Thread(this::deleteFile).start();
    }

    @FXML
    private void onActionButtonRename(ActionEvent actionEvent) {
        blockButton(buttonRename);
        setProgressIndicatorActivity(true, "Переименование файла...");
        renameFile();
    }

    private void downloadFile() {
        clientApp.send(new FileRequestMessage(getActiveRowFileName()));
    }

    private void deleteFile() {
        clientApp.send(new FileDeleteMessage(getActiveRowFileName()));
    }

    private void renameFile() {

        String oldName = getActiveRowFileName();

        TextInputDialog dialog = new TextInputDialog(oldName);
        dialog.setTitle("Переименование");
        dialog.setHeaderText("Переименование файла на сервере");
        dialog.setContentText("Введите новое имя файла:");

        // TODO it does not work correctly... some buggies is here
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

        TextFormatter<String> textFormatter = new TextFormatter<>(change -> {
            String newName = change.getControlNewText();
            Matcher matcher = pattern.matcher(newName);

            if (!matcher.matches()) {
                change.setText("");
                return null;
            } else {
                return change;
            }
        });

        dialog.getEditor().setTextFormatter(textFormatter);
        Optional<String> result = dialog.showAndWait();

        result.ifPresent(newName -> clientApp.send(new FileRenameMessage(oldName, newName)));
    }

    private String getActiveRowFileName() {
        return tableData.get(tableFiles.getSelectionModel().getFocusedIndex()).getFile().getName();
    }

    public void fillTable(ArrayList<java.io.File> files) {
        tableData.clear();
        for (File file : files) {
            tableData.add(new TableRowData(file));
        }
        tableFiles.setItems(tableData);
    }

    public void setProgressIndicatorActivity(boolean visible, String message) {
        progressIndicator.setVisible(visible);
        progressLabel.setText(visible ? message : "");
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
}
