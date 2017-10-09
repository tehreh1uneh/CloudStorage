package com.tehreh1uneh.cloudstorage.client.mainscreen;

import com.tehreh1uneh.cloudstorage.common.SocketThread;
import com.tehreh1uneh.cloudstorage.common.SocketThreadListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainScreen implements Initializable, SocketThreadListener, Thread.UncaughtExceptionHandler {

    private Stage stage;
    private SocketThread socketThread;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setSocketThread(SocketThread socketThread) {
        this.socketThread = socketThread;
    }

    @FXML
    private void onActionSourceCode() {

        String url = "https://github.com/tehreh1uneh/CloudStorage";
        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            try {
                desktop.browse(new URI(url));
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void onActionExit() {
        System.exit(0);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    //region SocketThreadEvents
    @Override
    public void onStartSocketThread(SocketThread socketThread) {

    }

    @Override
    public void onStopSocketThread(SocketThread socketThread) {

    }

    @Override
    public void onReadySocketThread(SocketThread socketThread, Socket socket) {

    }

    @Override
    public void onReceiveMessageSocketThread(SocketThread socketThread, Socket socket, byte[] value) {

    }

    @Override
    public void onExceptionSocketThread(SocketThread socketThread, Socket socket, Exception e) {

    }
    //endregion

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {

    }
}
