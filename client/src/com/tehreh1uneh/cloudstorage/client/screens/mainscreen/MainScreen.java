package com.tehreh1uneh.cloudstorage.client.screens.mainscreen;

import com.tehreh1uneh.cloudstorage.client.screens.BaseScreen;
import com.tehreh1uneh.cloudstorage.common.messages.FileMessage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.FileChooser;
import org.apache.log4j.Logger;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public final class MainScreen extends BaseScreen implements Initializable {

    private static final Logger logger = Logger.getLogger(MainScreen.class);

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
}
