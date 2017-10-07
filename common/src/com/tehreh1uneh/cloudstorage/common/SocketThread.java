package com.tehreh1uneh.cloudstorage.common;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class SocketThread extends Thread {

    private final SocketThreadListener eventListener;
    private final Socket socket;
    private ObjectOutputStream out;

    public SocketThread(SocketThreadListener eventListener, String name, Socket socket) {
        super(name);
        this.eventListener = eventListener;
        this.socket = socket;
        start();
    }

    @Override
    public void run() {
        eventListener.onStartSocketThread(this);
        try {
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            out = new ObjectOutputStream(socket.getOutputStream());
            eventListener.onReadySocketThread(this, socket);

            while (!isInterrupted()) {
                try {
                    Message msg = (Message) in.readObject();
                    eventListener.onReceiveMessageSocketThread(this, socket, msg);
                } catch (ClassNotFoundException e) {
                    eventListener.onExceptionSocketThread(this, socket, e);
                }
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

    public synchronized void send(Message msg) {
        try {
            out.writeObject(msg);
            out.flush();
        } catch (IOException e) {
            eventListener.onExceptionSocketThread(this, socket, e);
            close();
        }
    }

    private synchronized void close() {
        interrupt();
        try {
            socket.close();
        } catch (IOException e) {
            eventListener.onExceptionSocketThread(this, socket, e);
        }
    }
}
