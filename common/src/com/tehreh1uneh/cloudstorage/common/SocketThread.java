package com.tehreh1uneh.cloudstorage.common;

import com.tehreh1uneh.cloudstorage.common.messages.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class SocketThread extends Thread {

    private SocketThreadListener eventListener;

    private final Socket socket;
    private ObjectOutputStream out;

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
            out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            eventListener.onReadySocketThread(this, socket);

            while (!isInterrupted()) {

                Message message;
                try {
                    message = (Message) in.readObject();
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
                eventListener.onReceiveMessageSocketThread(this, socket, message);
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

    public synchronized void close() {
        interrupt();
        try {
            socket.close();
        } catch (IOException e) {
            eventListener.onExceptionSocketThread(this, socket, e);
        }
    }
}
