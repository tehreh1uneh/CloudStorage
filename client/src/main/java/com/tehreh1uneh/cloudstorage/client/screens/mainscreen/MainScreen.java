package com.tehreh1uneh.cloudstorage.client.screens.mainscreen;

import com.tehreh1uneh.cloudstorage.client.screens.BaseScreen;
import com.tehreh1uneh.cloudstorage.common.messages.FileMessage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import org.apache.log4j.Logger;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public final class MainScreen extends BaseScreen implements Initializable {

    private static final Logger logger = Logger.getLogger(MainScreen.class);
    @FXML
    TableColumn<TableRowData, String> colType;
    @FXML
    TableColumn<TableRowData, String> colSize;
    @FXML
    TableView tableFiles;
    @FXML
    private TableColumn<TableRowData, String> colFileName;
    @FXML
    private TableColumn<TableRowData, String> colModified;
    private ObservableList<TableRowData> tableData = FXCollections.observableArrayList();

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");

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
    private void onActionSync() {
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        colFileName.setCellValueFactory(new PropertyValueFactory<>("fileName"));
        colModified.setCellValueFactory(new PropertyValueFactory<>("modified"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colSize.setCellValueFactory(new PropertyValueFactory<>("size"));

        logger.info("Инициалищация основного экрана успешно завершена");
    }

    @FXML
    private void onActionLogOut() {
        logger.info("Попытка разлогиниться");
        clientApp.logOut();
    }

    @FXML
    private void sendFiles(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выберите файлы для передачи");

        List<File> files = fileChooser.showOpenMultipleDialog(clientApp.getStage());
        if (files != null) {
            for (File file : files) {
                clientApp.send(new FileMessage(file));
            }
        }
    }

    public void fillTable(ArrayList<File> files) {
        tableData.clear();

        for (int i = 0; i < files.size(); i++) {

            File file = files.get(i);

            tableData.add(new TableRowData(
                    fileNameWithoutExt(file.getName()),
                    dateFormat.format(file.lastModified()),
                    fileExt(file.getName()),
                    byteSizeToString(file.length())));
        }

        tableFiles.setItems(tableData);
    }

    private String byteSizeToString(long length) {
        if (length < 1024) {
            return Long.toString(length) + " байт";
        } else if (length < 1024 * 1024) {
            long left = length / 1024;
            int right = (int) ((double) (length % 1024) / 1024 * 100);

            return Long.toString(left) + "." + Integer.toString(right) + " КБ";
        } else {
            long left = length / (1024 * 1024);
            int right = (int) ((double) (length % (1024 * 1024)) / (1024 * 1024) * 100);

            return Long.toString(left) + "." + Integer.toString(right) + " МБ";
        }
    }

    private String fileExt(String fileName) {
        if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        } else {
            return "";
        }
    }

    private String fileNameWithoutExt(String fileName) {
        if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
            return fileName.substring(0, fileName.lastIndexOf("."));
        } else {
            return fileName;
        }

    }


}
