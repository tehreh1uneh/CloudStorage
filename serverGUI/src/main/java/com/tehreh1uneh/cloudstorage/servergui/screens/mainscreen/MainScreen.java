package com.tehreh1uneh.cloudstorage.servergui.screens.mainscreen;

import com.tehreh1uneh.cloudstorage.servergui.screenmanager.ServerApp;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import org.apache.log4j.Logger;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

public final class MainScreen implements Initializable {

    private static final Logger logger = Logger.getLogger(MainScreen.class);
    private ServerApp serverApp;

    //region View fields

    @FXML
    private Hyperlink sourceCodeLink;
    @FXML
    private Button buttonStart;
    @FXML
    private Button buttonStop;
    //endregion

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        logger.info("GUI сервера успешно инициализирован");
    }

    //region View events

    @FXML
    private void onClickStartServer() {
        serverApp.turnOnServer();
    }

    @FXML
    private void onClickStopServer() {
        serverApp.turnOffServer();
    }

    @FXML
    private void openSourceCodeLink() {
        String url = "https://github.com/tehreh1uneh/CloudStorage";

        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().browse(new URI(url));
                sourceCodeLink.setText("Source code");
                logger.info("Открыта ссылка на исходный код");
            } catch (IOException | URISyntaxException e) {
                logger.warn("Не удалось открыть ссылку на исходный код", e);
            }
        } else {
            sourceCodeLink.setText(url);
            logger.info("Ссылка на исходный код не может быть открыта. Desktop is not supported");
        }
    }

    //endregion

    public void blockButtonStart() {
        buttonStart.setDisable(true);
        buttonStop.setDisable(false);
    }

    public void blockButtonStop() {
        buttonStop.setDisable(true);
        buttonStart.setDisable(false);
    }

    public void setServerApp(ServerApp serverApp) {
        this.serverApp = serverApp;
    }
}
