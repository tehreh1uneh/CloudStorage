package com.tehreh1uneh.cloudstorage.client.mainscreen;

import com.tehreh1uneh.cloudstorage.common.SocketThread;
import com.tehreh1uneh.cloudstorage.common.SocketThreadListener;
import javafx.fxml.Initializable;

import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class MainScreen implements Initializable, SocketThreadListener, Thread.UncaughtExceptionHandler {

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

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
