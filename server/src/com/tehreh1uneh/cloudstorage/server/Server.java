package com.tehreh1uneh.cloudstorage.server;

import com.tehreh1uneh.cloudstorage.common.*;

import java.net.ServerSocket;
import java.net.Socket;

public class Server implements ServerSocketThreadListener, SocketThreadListener {

    private static final int TIMEOUT = 2_000;

    private ServerSocketThread serverSocketThread;
    private ServerListener serverListener;

    public Server(ServerListener serverListener) {
        this.serverListener = serverListener;
    }

    public void startServer(int port) {
        if (serverSocketThread != null && serverSocketThread.isAlive()) {
            log("Сервер был запущен ранее\n");
            return;
        }
        serverSocketThread = new ServerSocketThread(this, "ServerSocketThread", port, TIMEOUT);
        log("Сервер успешно запущен\n");
    }

    public void stopServer() {

    }

    //region ServerSocketThread
    @Override
    public void onStartServerSocketThread(ServerSocketThread thread) {

    }

    @Override
    public void onStopServerSocketThread(ServerSocketThread thread) {

    }

    @Override
    public void onReadyServerSocketThread(ServerSocketThread thread, ServerSocket serverSocket) {

    }

    @Override
    public void onAcceptedSocket(ServerSocketThread thread, ServerSocket serverSocket, Socket socket) {

    }

    @Override
    public void onTimeOutAccept(ServerSocketThread thread, ServerSocket serverSocket) {

    }

    @Override
    public void onServerSocketThreadException(ServerSocketThread thread, Exception e) {

    }
    //endregion

    //region SocketThread
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
    public void onReceiveMessageSocketThread(SocketThread socketThread, Socket socket, Message value) {

    }

    @Override
    public void onExceptionSocketThread(SocketThread socketThread, Socket socket, Exception e) {

    }
    //endregion

    private void log(String msg) {
        serverListener.log(this, msg);
    }

}
