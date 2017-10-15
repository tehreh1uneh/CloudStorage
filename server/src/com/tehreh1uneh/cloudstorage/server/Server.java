package com.tehreh1uneh.cloudstorage.server;

import com.tehreh1uneh.cloudstorage.common.ServerSocketThread;
import com.tehreh1uneh.cloudstorage.common.ServerSocketThreadListener;
import com.tehreh1uneh.cloudstorage.common.SocketThread;
import com.tehreh1uneh.cloudstorage.common.SocketThreadListener;
import com.tehreh1uneh.cloudstorage.common.messages.*;
import com.tehreh1uneh.cloudstorage.server.authorization.AuthorizeManager;
import com.tehreh1uneh.cloudstorage.server.authorization.DatabaseController;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Server implements ServerSocketThreadListener, SocketThreadListener {

    private static final Logger logger = Logger.getLogger(Server.class);
    private AuthorizeManager authorizeManager;
    private ServerSocketThread serverSocketThread;
    private boolean blocked = false;


    public void turnOn(int port, int timeout) {
        if (serverSocketThread != null && serverSocketThread.isAlive()) {
            logger.info("Сервер был запущен ранее");
            return;
        }
        serverSocketThread = new ServerSocketThread(this, "ServerSocketThread", port, timeout);
        logger.info("Сервер успешно запущен");
        authorizeManager = new DatabaseController();
        authorizeManager.initialize();
        logger.info("Подключение к СУБД инициализировано");
    }

    public void turnOff() {
        if (serverSocketThread == null || !serverSocketThread.isAlive()) {
            logger.info("Сервер не запущен");
            return;
        }
        if (!blocked) {
            blocked = true;
            authorizeManager.dispose();
            logger.info("Соединение с СУБД разорвано");
            serverSocketThread.interrupt();
            logger.info("Серверу отправлен interrupt");
        }
    }

    private void handleAuthorizedClient(ClientSocketThread client, Message message) {
        if (message.getType() == MessageType.DISCONNECT) {
            client.close();
        } else if (message.getType() == MessageType.FILE) {
            handleFileMessage((FileMessage) message);
        }
    }

    private void handleUnauthorizedClient(ClientSocketThread client, Socket socket, Message message) {

        if (message.getType() == MessageType.AUTH_REQUEST) {
            AuthRequestMessage authRequestMessage = (AuthRequestMessage) message;
            client.setAuthorized(authorizeManager.authorize(authRequestMessage.getLogin(), authRequestMessage.getPassword()));
            AuthResponseMessage response = new AuthResponseMessage(client.isAuthorized(), client.isAuthorized() ? "" : "Неверный логин или пароль");
            client.send(response);
        }

        if (!client.isAuthorized()) {
            client.close();
            logger.info("Ошибка авторизации клиента: " + socket.toString());
        } else {
            logger.info("Клиент успешно авторизован: " + socket.toString());
        }
    }

    private void handleFileMessage(FileMessage message) {
        try {
            Files.write(Paths.get("C:/Users/eurythmic/Desktop/123/" + message.getName()), message.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    //region ServerSocketThread
    @Override
    public void onStartServerSocketThread(ServerSocketThread thread) {
        logger.info("Серверный сокет стартован");
    }

    @Override
    public void onStopServerSocketThread(ServerSocketThread thread) {
        logger.info("Сервер остановлен");
        blocked = false;
        if (authorizeManager != null) authorizeManager.dispose();
    }

    @Override
    public void onReadyServerSocketThread(ServerSocketThread thread, ServerSocket serverSocket) {
        logger.info("Серверный сокет готов к работе");
    }

    @Override
    public void onAcceptedSocket(ServerSocketThread thread, ServerSocket serverSocket, Socket socket) {
        logger.info("Клиент присоединился: " + socket.toString());
        String threadName = "Socket thread: " + socket.getInetAddress() + ":" + socket.getPort();
        new ClientSocketThread(this, threadName, socket);
    }

    @Override
    public void onTimeOutAccept(ServerSocketThread thread, ServerSocket serverSocket) {
        logger.trace("Таймаут серверного сокета");
    }

    @Override
    public void onServerSocketThreadException(ServerSocketThread thread, Exception e) {
        logger.warn("Ошибка серверного сокета", e);
        if (authorizeManager != null) authorizeManager.dispose();
    }
    //endregion

    //region SocketThread
    @Override
    public void onStartSocketThread(SocketThread socketThread) {
        logger.info("Клиентский сокет стартован на сервере");
    }

    @Override
    public void onStopSocketThread(SocketThread socketThread) {
        logger.info("Клиентский сокет остановлен на сервере");
    }

    @Override
    public void onReadySocketThread(SocketThread socketThread, Socket socket) {
        logger.info("Клиентский сокет на сервере готов к работе");
    }

    @Override
    public void onReceiveMessageSocketThread(SocketThread socketThread, Socket socket, Message message) {
        ClientSocketThread client = (ClientSocketThread) socketThread;
        if (client.isAuthorized()) {
            handleAuthorizedClient(client, message);
        } else {
            handleUnauthorizedClient(client, socket, message);
        }
    }

    @Override
    public void onExceptionSocketThread(SocketThread socketThread, Socket socket, Exception e) {
        logger.fatal("Ошибка в клиентском сокете: " + socket.getInetAddress() + ":" + socket.getPort(), e);
        try {
            socket.close();
        } catch (IOException es) {
            logger.error("Не удалось закрыть сокет, в котором возникла ошибка.", es);
        }
    }
    //endregion
}
