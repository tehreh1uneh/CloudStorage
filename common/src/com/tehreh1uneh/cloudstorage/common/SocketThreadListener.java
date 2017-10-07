package com.tehreh1uneh.cloudstorage.common;

import java.net.Socket;

public interface SocketThreadListener {

    void onStartSocketThread(SocketThread socketThread);

    void onStopSocketThread(SocketThread socketThread);

    void onReadySocketThread(SocketThread socketThread, Socket socket);

    void onReceiveMessageSocketThread(SocketThread socketThread, Socket socket, Message value);

    void onExceptionSocketThread(SocketThread socketThread, Socket socket, Exception e);
}
