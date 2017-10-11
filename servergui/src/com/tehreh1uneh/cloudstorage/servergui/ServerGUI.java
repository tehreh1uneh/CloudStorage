package com.tehreh1uneh.cloudstorage.servergui;

import com.tehreh1uneh.cloudstorage.server.LogListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextArea;

import java.net.URL;
import java.util.ResourceBundle;

public final class ServerGUI implements Initializable, LogListener {

    @FXML
    public Hyperlink sourceCodeLink;
    @FXML
    private TextArea logArea;
    private ServerGUILauncher serverGUILauncher;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        log("Инициализация оболочки сервера успешно завершена");
    }

    void setServerGUILauncher(ServerGUILauncher serverGUILauncher) {
        this.serverGUILauncher = serverGUILauncher;
    }

    @FXML
    private void onClickStartServer() {
        serverGUILauncher.turnOnServer();
    }

    @FXML
    private void onClickStopServer() {
        serverGUILauncher.turnOffServer();
    }

    @FXML
    private void openSourceCodeLink() {
        serverGUILauncher.openSourceCodeLink();
    }

    @SuppressWarnings("ForLoopReplaceableByForEach")
    @Override
    public void log(String... msg) {
        for (int i = 0; i < msg.length; i++) logArea.appendText(msg[i]);
        logArea.appendText("\n");
    }
}
