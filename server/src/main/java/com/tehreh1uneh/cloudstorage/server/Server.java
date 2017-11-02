package com.tehreh1uneh.cloudstorage.server;

import com.tehreh1uneh.cloudstorage.common.ServerSocketThread;
import com.tehreh1uneh.cloudstorage.common.ServerSocketThreadListener;
import com.tehreh1uneh.cloudstorage.common.SocketThread;
import com.tehreh1uneh.cloudstorage.common.SocketThreadListener;
import com.tehreh1uneh.cloudstorage.common.messages.ErrorMessage;
import com.tehreh1uneh.cloudstorage.common.messages.auth.AuthRequestMessage;
import com.tehreh1uneh.cloudstorage.common.messages.auth.AuthResponseMessage;
import com.tehreh1uneh.cloudstorage.common.messages.base.Message;
import com.tehreh1uneh.cloudstorage.common.messages.base.MessageType;
import com.tehreh1uneh.cloudstorage.common.messages.files.*;
import com.tehreh1uneh.cloudstorage.common.messages.registration.RegistrationRequestMessage;
import com.tehreh1uneh.cloudstorage.common.messages.registration.RegistrationResponseMessage;
import com.tehreh1uneh.cloudstorage.common.notification.Notifier;
import com.tehreh1uneh.cloudstorage.server.authorization.AuthorizeManager;
import com.tehreh1uneh.cloudstorage.server.authorization.DatabaseController;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import static com.tehreh1uneh.cloudstorage.server.Config.FILE_SEPARATOR;
import static com.tehreh1uneh.cloudstorage.server.Config.STORAGE_PATH;

public class Server implements ServerSocketThreadListener, SocketThreadListener {

    private static final Logger logger = Logger.getLogger(Server.class);
    private AuthorizeManager authorizeManager;
    private ServerSocketThread serverSocketThread;
    private boolean blocked = false;
    private final ServerListener listener;

    public Server(ServerListener listener) {
        this.listener = listener;
    }

    public void turnOn(int port, int timeout) {
        if (serverSocketThread != null && serverSocketThread.isAlive()) {
            Notifier.show(5d, "Сервер", "Сервер был запущен ранее", Notifier.NotificationType.INFORMATION);
            logger.info("Сервер был запущен ранее");
            return;
        }
        serverSocketThread = new ServerSocketThread(this, "ServerSocketThread", port, timeout);
        logger.info("Сервер запущен");
        authorizeManager = new DatabaseController();
        authorizeManager.initialize();
        logger.info("Подключение к СУБД инициализировано");
        listener.onConnect();
    }

    public void turnOff() {
        if (serverSocketThread == null || !serverSocketThread.isAlive()) {
            Notifier.show(5d, "Сервер", "Сервер не запущен", Notifier.NotificationType.INFORMATION);
            logger.info("Сервер не запущен");
            return;
        }
        if (!blocked) {
            blocked = true;
            authorizeManager.dispose();
            logger.info("Соединение с СУБД разорвано");
            serverSocketThread.interrupt();
            logger.info("Серверу отправлен interrupt");
            listener.onDisconnect();
        }
    }

    //region Message handlers

    private void handleAuthorizedClient(ClientSocketThread client, Message message) {
        if (message.getType() == MessageType.DISCONNECT) {
            disconnectClient(client);
        } else if (message.getType() == MessageType.FILE) {
            handleFileMessage((FileMessage) message, client);
        } else if (message.getType() == MessageType.FILE_REQUEST) {
            handleFileRequest((FileRequestMessage) message, client);
        } else if (message.getType() == MessageType.FILES_LIST_REQUEST) {
            FilesListRequest request = (FilesListRequest) message;
            if (request.isUp()) {
                client.setCurrentPath(Paths.get(client.getCurrentPath() + FILE_SEPARATOR + "..").normalize().toString() + FILE_SEPARATOR);
            } else if (request.isDirectory()) {
                client.setCurrentPath(request.getDirectoryPath());
            } else {
                client.setCurrentPath("");
            }
            sendFilesList(client);
        } else if (message.getType() == MessageType.FILE_DELETE) {
            deleteFile(client, (FileDeleteMessage) message);
        } else if (message.getType() == MessageType.FILE_RENAME) {
            renameFile(client, (FileRenameMessage) message);
        } else {
            logger.fatal("Необрабатываемый тип сообщения: " + message.getType());
            throw new RuntimeException();
        }
    }

    private void handleUnauthorizedClient(ClientSocketThread client, Socket socket, Message message) {
        // TODO authorized clients - client thread map
        if (message.getType() == MessageType.AUTH_REQUEST) {
            AuthRequestMessage authRequestMessage = (AuthRequestMessage) message;
            client.setAuthorized(authorizeManager.authorize(authRequestMessage.getLogin(), authRequestMessage.getPassword()));

            AuthResponseMessage response = new AuthResponseMessage(client.isAuthorized(), client.isAuthorized() ? "" : "Неверный логин или пароль");
            client.send(response);

            if (!client.isAuthorized()) {
                logger.info("Ошибка авторизации клиента: " + socket.toString());
                disconnectClient(client);
            } else {
                client.setLogin(authRequestMessage.getLogin());

                try {
                    createUserPath(client);
                    sendFilesList(client);
                } catch (IOException e) {
                    String errorMessage = "Не удалось создать папку для хранения данных пользователя";
                    logger.error(errorMessage, e);
                    client.send(new ErrorMessage(errorMessage, true));
                    disconnectClient(client);
                    return;
                }
                logger.info("Клиент авторизован: " + socket.toString());
            }
        } else if (message.getType() == MessageType.REG_REQUEST) {

            RegistrationRequestMessage regMessage = (RegistrationRequestMessage) message;
            if (authorizeManager.register(regMessage.getLogin(), regMessage.getPassword())) {
                logger.info("Пользователь (" + regMessage.getLogin() + ") зарегистрирован: " + socket);
                client.send(new RegistrationResponseMessage(true, "Пользователь зарегистрирован"));
            } else {
                logger.info("Пользователь (" + regMessage.getLogin() + ") уже существует: " + socket);
                client.send(new RegistrationResponseMessage(false, "Пользователь с таким логином уже существует"));
            }
            disconnectClient(client);
        } else {
            logger.fatal("Необрабатываемый тип сообщения: " + message.getType());
            throw new RuntimeException();
        }
    }

    private void handleFileMessage(FileMessage message, ClientSocketThread client) {
        try {
            // TODO check file name collisions

            String currentPath = client.getCurrentPath().isEmpty() ? client.getPath() : client.getCurrentPath();
            Files.write(Paths.get(currentPath + FILE_SEPARATOR + message.getName()), message.getBytes());
            sendFilesList(client);
        } catch (IOException e) {
            logger.error("Пользователь: " + client.getLogin() + ". Ошибка сохранения файла");
            client.send(new ErrorMessage("Не удалось сохранить файл", false));
        }
    }

    private void handleFileRequest(FileRequestMessage message, ClientSocketThread client) {
        File file = message.getFile();
        if (!file.isDirectory()) {
            client.send(new FileMessage(new File(file.getPath())));
        } else {
            client.send(new ErrorMessage("Нельзя скачивать папки", false));
        }
    }

    private void deleteFile(ClientSocketThread client, FileDeleteMessage message) {
        Path path = Paths.get(message.getFile().getPath());
        if (Files.exists(path)) {
            try {
                Files.delete(path);
                sendFilesList(client);
            } catch (IOException e) {
                logger.warn("Пользователь: " + client.getLogin() + ". Не удалось удалить файл: " + path, e);
                client.send(new ErrorMessage("Не удалось удалить файл", false));
            }
        }
    }

    private void renameFile(ClientSocketThread client, FileRenameMessage message) {
        Path oldPath = Paths.get(message.getFile().getPath());
        Path newPath = Paths.get(message.getFile().getParent() + FILE_SEPARATOR + message.getNewName());
        if (Files.exists(oldPath) && oldPath.toFile().renameTo(newPath.toFile())) sendFilesList(client);
    }
    //endregion

    private synchronized void sendFilesList(ClientSocketThread client) {

        ArrayList<File> filesList = new ArrayList<>();
        boolean root = client.getCurrentPath().isEmpty() || client.getPath().equals(client.getCurrentPath());
        Path currentPath = Paths.get(root ? client.getPath() : client.getCurrentPath());

        try {
            Files.walk(currentPath, 1).forEach(it -> {
                if (it != currentPath) filesList.add(it.toFile());
            });
        } catch (IOException e) {
            String errorMessage = "Ошибка чтения списка файлов.";
            logger.error(errorMessage, e);
            client.send(new ErrorMessage(errorMessage, true));
            disconnectClient(client);
            return;
        }
        client.send(new FilesListResponse(filesList, root));
    }

    private void disconnectClient(SocketThread clientThread) {
        clientThread.interrupt();
    }

    private void createUserPath(ClientSocketThread client) throws IOException {
        client.setPath(STORAGE_PATH + client.getLogin() + FILE_SEPARATOR);
        Path path = Paths.get(client.getPath());
        if (Files.notExists(path)) {
            Files.createDirectories(path);
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
        String threadName = "ServerSide Socket thread: " + socket.getInetAddress() + ":" + socket.getPort();
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
