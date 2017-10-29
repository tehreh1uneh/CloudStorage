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

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;

import static com.tehreh1uneh.cloudstorage.server.Config.FILE_SEPARATOR;
import static com.tehreh1uneh.cloudstorage.server.Config.STORAGE_PATH;

public class Server implements ServerSocketThreadListener, SocketThreadListener {

    private static final Logger logger = Logger.getLogger(Server.class);
    private AuthorizeManager authorizeManager;
    private ServerSocketThread serverSocketThread;
    private boolean blocked = false;
    private ServerListener listener;

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

    private void handleAuthorizedClient(ClientSocketThread client, Message message) {
        if (message.getType() == MessageType.DISCONNECT) {
            disconnectClient(client);
        } else if (message.getType() == MessageType.FILE) {
            handleFileMessage((FileMessage) message, client);
        } else if (message.getType() == MessageType.FILE_REQUEST) {
            handleFileRequest((FileRequestMessage) message, client);
        } else if (message.getType() == MessageType.FILES_LIST_REQUEST) {
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

    private void handleFileMessage(FileMessage message, ClientSocketThread client) {
        try {
            // TODO check file name collisions
            Files.write(Paths.get(client.getPath() + message.getName()), message.getBytes());
            sendFilesList(client);
        } catch (IOException e) {
            logger.error("Пользователь: " + client.getLogin() + ". Ошибка сохранения файла");
            client.send(new ErrorMessage("Не удалось сохранить файл", false));
        }
    }

    private void handleFileRequest(FileRequestMessage message, ClientSocketThread client) {

        try {
            ArrayList<java.io.File> files = new ArrayList<>();
            String fileName = message.getFileName();

            Files.walkFileTree(Paths.get(STORAGE_PATH), new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if (!attrs.isDirectory()) {
                        if (file.getFileName().toString().equals(fileName)) {
                            files.add(new java.io.File(file.toUri()));
                        }
                    }
                    return FileVisitResult.CONTINUE;
                }
            });

            for (java.io.File file : files) {
                client.send(new FileMessage(file));
            }
        } catch (IOException e) {
            logger.warn("Запрошенный файл (" + message.getFileName() + ") не найден на сервере.", e);
            client.send(new ErrorMessage("Запрошенный файл (" + message.getFileName() + ") не найден на сервере.", false));
        }
    }

    private void deleteFile(ClientSocketThread client, FileDeleteMessage message) {
        Path path = Paths.get(client.getPath() + message.getFileName());
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
        Path oldPath = Paths.get(client.getPath() + message.getOldName());
        Path newPath = Paths.get(client.getPath() + message.getNewName());
        if (Files.exists(oldPath) && oldPath.toFile().renameTo(newPath.toFile())) sendFilesList(client);
    }

    private synchronized void sendFilesList(ClientSocketThread client) {

        ArrayList<java.io.File> filesList = new ArrayList<>();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(client.getPath()))) {
            for (Path entry : stream) {
                if (!Files.isDirectory(entry)) {
                    filesList.add(new java.io.File(entry.toUri()));
                }
            }
        } catch (IOException e) {
            String errorMessage = "Ошибка чтения списка файлов.";
            logger.error(errorMessage, e);
            client.send(new ErrorMessage(errorMessage, true));
            disconnectClient(client);
            return;
        }

        client.send(new FilesListResponse(filesList));
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
