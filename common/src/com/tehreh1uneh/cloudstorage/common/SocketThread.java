package com.tehreh1uneh.cloudstorage.common;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

public class SocketThread extends Thread {

    private SocketThreadListener eventListener;

    private final Socket socket;
    private BufferedOutputStream out;

    public SocketThread(SocketThreadListener eventListener, String name, Socket socket) {
        super(name);
        this.eventListener = eventListener;
        this.socket = socket;
        start();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public void run() {
        eventListener.onStartSocketThread(this);
        try {
            BufferedInputStream in = new BufferedInputStream(socket.getInputStream());
            out = new BufferedOutputStream(socket.getOutputStream());
            eventListener.onReadySocketThread(this, socket);

            while (!isInterrupted()) {
                if (in.available() == 0) continue;
                byte[] msg = new byte[in.available()];
                in.read(msg);
                eventListener.onReceiveMessageSocketThread(this, socket, msg);
            }
        } catch (IOException e) {
            eventListener.onExceptionSocketThread(this, socket, e);
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                eventListener.onExceptionSocketThread(this, socket, e);
            }
            eventListener.onStopSocketThread(this);
        }
    }

    public synchronized void send(byte[] msg) {
        try {
            out.write(msg);
            out.flush();
        } catch (IOException e) {
            eventListener.onExceptionSocketThread(this, socket, e);
            close();
        }
    }

    public synchronized void close() {
        interrupt();
        try {
            socket.close();
        } catch (IOException e) {
            eventListener.onExceptionSocketThread(this, socket, e);
        }
    }
}
