package com.tehreh1uneh.cloudstorage.common;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketTimeoutException;

public class ServerSocketThread extends Thread {

    private final int port;
    private final int timeout;
    private final ServerSocketThreadListener eventListener;

    public ServerSocketThread(ServerSocketThreadListener eventListener, String name, int port, int timeout) {
        super(name);
        this.eventListener = eventListener;
        this.port = port;
        this.timeout = timeout;
        start();
    }

    @Override
    public void run() {
        eventListener.onStartServerSocketThread(this);
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            serverSocket.setSoTimeout(timeout);
            eventListener.onReadyServerSocketThread(this, serverSocket);
            while (!isInterrupted()) {
                try {
                    eventListener.onAcceptedSocket(this, serverSocket, serverSocket.accept());
                } catch (SocketTimeoutException e) {
                    eventListener.onTimeOutAccept(this, serverSocket);
                }
            }
        } catch (IOException e) {
            eventListener.onServerSocketThreadException(this, e);
        } finally {
            eventListener.onStopServerSocketThread(this);
        }
    }
}