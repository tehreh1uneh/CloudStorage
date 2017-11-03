package com.tehreh1uneh.cloudstorage.common;

import com.tehreh1uneh.cloudstorage.common.messages.base.Message;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class SocketThread extends Thread {

    private static final Logger logger = Logger.getLogger(SocketThread.class);
    private final Socket socket;
    private final SocketThreadListener eventListener;
    private ObjectOutputStream out;
    private boolean busy = false;

    public SocketThread(SocketThreadListener eventListener, String name, Socket socket) {
        super(name);
        this.eventListener = eventListener;
        this.socket = socket;
        logger.info("Экземпляр SocketThread создан");
        start();
    }

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
                    logger.fatal("Ошибка при кастовании сообщения в потоке [" + getName() + "]", e);
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
        busy = true;
        try {
            out.writeObject(msg);
            out.flush();
        } catch (IOException e) {
            eventListener.onExceptionSocketThread(this, socket, e);
            busy = false;
            interrupt();
        }
        busy = false;
    }

    @Override
    public void interrupt() {
        new Thread(() -> {
            while (true) {
                if (!isAlive()) break;
                if (!busy) {
                    super.interrupt();
                    logger.info("Thread [" + getName() + "] успешно остановлен");
                    eventListener.onStopSocketThread(this);
                    break;
                }
            }
        }).start();
    }
}
