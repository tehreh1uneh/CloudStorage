package com.tehreh1uneh.cloudstorage.server;

import com.tehreh1uneh.cloudstorage.common.ServerSocketThread;
import com.tehreh1uneh.cloudstorage.common.ServerSocketThreadListener;
import com.tehreh1uneh.cloudstorage.common.SocketThread;
import com.tehreh1uneh.cloudstorage.common.SocketThreadListener;
import com.tehreh1uneh.cloudstorage.common.messages.AuthRequestMessage;
import com.tehreh1uneh.cloudstorage.common.messages.AuthResponseMessage;
import com.tehreh1uneh.cloudstorage.common.messages.Message;
import com.tehreh1uneh.cloudstorage.common.messages.MessageType;
import com.tehreh1uneh.cloudstorage.common.messages.util.Converter;
import com.tehreh1uneh.cloudstorage.server.Authorization.AuthorizeManager;
import com.tehreh1uneh.cloudstorage.server.Authorization.DatabaseController;

import java.net.ServerSocket;
import java.net.Socket;

public class Server implements ServerSocketThreadListener, SocketThreadListener {

    private final LogListener logListener;
    private AuthorizeManager authorizeManager;
    private ServerSocketThread serverSocketThread;
    private Converter converter = new Converter();
    private boolean blocked = false;

    public Server(LogListener logListener) {
        this.logListener = logListener;
    }

    public void turnOn(int port, int timeout) {
        if (serverSocketThread != null && serverSocketThread.isAlive()) {
            log("Сервер был запущен ранее");
            return;
        }
        serverSocketThread = new ServerSocketThread(this, "ServerSocketThread", port, timeout);
        log("Сервер успешно запущен");
        authorizeManager = new DatabaseController();
        authorizeManager.initialize();
        log("Подключение к СУБД инициализировано");
    }

    public void turnOff() {
        if (serverSocketThread == null || !serverSocketThread.isAlive()) {
            log("Сервер не запущен");
            return;
        }
        if (!blocked) {
            blocked = true;
            authorizeManager.dispose();
            log("Соединение с СУБД разорвано");
            serverSocketThread.interrupt();
            log("Сервер останавливается...");
        }
    }

    private void handleAuthorizedClient(ClientSocketThread client, Message message) {
        if (message.getType() == MessageType.DISCONNECT) {
            client.close();
        }
    }

    private void handleUnauthorizedClient(ClientSocketThread client, Socket socket, Message message) {

        if (message.getType() == MessageType.AUTH_REQUEST) {
            AuthRequestMessage authRequestMessage = (AuthRequestMessage) message;
            client.setAuthorized(authorizeManager.authorize(authRequestMessage.getLogin(), authRequestMessage.getPassword()));
            AuthResponseMessage response = new AuthResponseMessage(client.isAuthorized(), client.isAuthorized() ? "" : "Неверный логин или пароль");
            client.send(converter.objectToBytes(response));
        }

        if (!client.isAuthorized()) {
            client.close();
            log("Клиент не авторизован, соединение будет разорвано: ", socket.toString());
        } else {
            log("Успешная авторизация: ", socket.toString());
        }
    }

    //region ServerSocketThread
    @Override
    public void onStartServerSocketThread(ServerSocketThread thread) {
        log("Серверный сокет стартован");
    }

    @Override
    public void onStopServerSocketThread(ServerSocketThread thread) {
        log("Сервер остановлен");
        blocked = false;
    }

    @Override
    public void onReadyServerSocketThread(ServerSocketThread thread, ServerSocket serverSocket) {
        log("Серверный сокет готов к работе");
    }

    @Override
    public void onAcceptedSocket(ServerSocketThread thread, ServerSocket serverSocket, Socket socket) {
        log("Клиент присоединился: ", socket.toString());
        String threadName = "Socket thread: " + socket.getInetAddress() + ":" + socket.getPort();
        new ClientSocketThread(this, threadName, socket);
    }

    @Override
    public void onTimeOutAccept(ServerSocketThread thread, ServerSocket serverSocket) {
        log("Таймаут серверного сокета");
    }

    @Override
    public void onServerSocketThreadException(ServerSocketThread thread, Exception e) {
        log("Ошибка серверного сокета");
        if (authorizeManager != null) authorizeManager.dispose();

    }
    //endregion

    //region SocketThread
    @Override
    public void onStartSocketThread(SocketThread socketThread) {
        log("Сокет стартован на сервере");
    }

    @Override
    public void onStopSocketThread(SocketThread socketThread) {
        log("Остановлен клиентский сокет на сервере");
    }

    @Override
    public void onReadySocketThread(SocketThread socketThread, Socket socket) {
        log("Сокет на сервере готов к работе");
    }

    @Override
    public void onReceiveMessageSocketThread(SocketThread socketThread, Socket socket, byte[] value) {
        ClientSocketThread client = (ClientSocketThread) socketThread;
        Message message = converter.bytesToMessage(value);

        if (client.isAuthorized()) {
            handleAuthorizedClient(client, message);
        } else {
            handleUnauthorizedClient(client, socket, message);
        }
    }

    @Override
    public void onExceptionSocketThread(SocketThread socketThread, Socket socket, Exception e) {
        log("Сокет на сервере получил ошибку");
    }
    //endregion

    private void log(String... msg) {
        logListener.log(msg);
    }
}
