package com.tehreh1uneh.cloudstorage.servergui.screens.mainscreen;

import com.tehreh1uneh.cloudstorage.server.LogListener;
import com.tehreh1uneh.cloudstorage.servergui.screenmanager.ServerApp;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextArea;

import java.net.URL;
import java.util.ResourceBundle;

public final class MainScreen implements Initializable, LogListener {

    @FXML
    public Hyperlink sourceCodeLink;
    @FXML
    private TextArea logArea;
    private ServerApp serverApp;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        log("Инициализация оболочки сервера успешно завершена");
    }

    public void setServerApp(ServerApp serverApp) {
        this.serverApp = serverApp;
    }

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
        serverApp.openSourceCodeLink();
    }

    @SuppressWarnings("ForLoopReplaceableByForEach")
    @Override
    public void log(String... msg) {
        for (int i = 0; i < msg.length; i++) logArea.appendText(msg[i]);
        logArea.appendText("\n");
    }
}
