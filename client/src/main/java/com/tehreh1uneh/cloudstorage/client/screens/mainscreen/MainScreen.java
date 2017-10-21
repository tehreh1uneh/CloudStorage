package com.tehreh1uneh.cloudstorage.client.screens.mainscreen;

import com.tehreh1uneh.cloudstorage.client.screens.BaseScreen;
import com.tehreh1uneh.cloudstorage.common.messages.files.FileDel;
import com.tehreh1uneh.cloudstorage.common.messages.files.FileReq;
import com.tehreh1uneh.cloudstorage.common.messages.files.FileResp;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
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
import java.util.ResourceBundle;

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
    private ObservableList<TableRowData> tableData = FXCollections.observableArrayList();


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

    @FXML
    private void onTableMouseClick(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() > 1) {
            clientApp.send(new FileReq(getActiveRowFileName()));
        }
    }

    @FXML
    private void onTableKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            clientApp.send(new FileReq(getActiveRowFileName()));
        } else if (keyEvent.getCode() == KeyCode.DELETE) {
            clientApp.send(new FileDel(getActiveRowFileName()));
        }
    }

    private String getActiveRowFileName() {
        return tableData.get(tableFiles.getSelectionModel().getFocusedIndex()).getFile().getName();
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
                clientApp.send(new FileResp(file));
            }
        }
    }

    public void fillTable(ArrayList<File> files) {
        tableData.clear();
        for (int i = 0; i < files.size(); i++) {
            tableData.add(new TableRowData(files.get(i)));
        }
        tableFiles.setItems(tableData);
    }



}
