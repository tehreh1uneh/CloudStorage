package com.tehreh1uneh.cloudstorage.client.screenmanager;

import com.tehreh1uneh.cloudstorage.client.screens.BaseScreen;
import com.tehreh1uneh.cloudstorage.client.screens.authscreen.AuthScreen;
import com.tehreh1uneh.cloudstorage.client.screens.mainscreen.MainScreen;
import com.tehreh1uneh.cloudstorage.client.screens.registrationscreen.RegistrationScreen;
import com.tehreh1uneh.cloudstorage.common.SocketThread;
import com.tehreh1uneh.cloudstorage.common.SocketThreadListener;
import com.tehreh1uneh.cloudstorage.common.messages.DisconnectMessage;
import com.tehreh1uneh.cloudstorage.common.messages.ErrorMessage;
import com.tehreh1uneh.cloudstorage.common.messages.auth.AuthRequestMessage;
import com.tehreh1uneh.cloudstorage.common.messages.auth.AuthResponseMessage;
import com.tehreh1uneh.cloudstorage.common.messages.base.Message;
import com.tehreh1uneh.cloudstorage.common.messages.base.MessageType;
import com.tehreh1uneh.cloudstorage.common.messages.files.FileMessage;
import com.tehreh1uneh.cloudstorage.common.messages.files.FilesListResponse;
import com.tehreh1uneh.cloudstorage.common.messages.registration.RegistrationRequestMessage;
import com.tehreh1uneh.cloudstorage.common.messages.registration.RegistrationResponseMessage;
import com.tehreh1uneh.cloudstorage.common.notification.Notifier;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import static com.tehreh1uneh.cloudstorage.client.screenmanager.Config.*;

// TODO create ?observers for screens

public final class ClientApp extends Application implements SocketThreadListener, Thread.UncaughtExceptionHandler {

    private static final Logger logger = Logger.getLogger(ClientApp.class);

    //region Screens

    private Stage stage;
    private SocketThread socketThread;
    private BaseScreen screen;

    @Override
    public void start(Stage stage) throws Exception {
        Thread.setDefaultUncaughtExceptionHandler(this);
        this.stage = stage;
        setAuthScreen();
        logger.info("Клиентский GUI запущен");
    }

    public void setAuthScreen() {
        try {
            replaceSceneContent(AUTH_VIEW_PATH);

            stage.setOnCloseRequest(windowEvent -> {
                disconnect(true);
                System.exit(0);
            });

            stage.setTitle(AUTH_VIEW_TITLE);
            stage.setResizable(false);
            stage.setWidth(AUTH_VIEW_WIDTH);
            stage.setHeight(AUTH_VIEW_HEIGHT);
            stage.show();

            logger.info("Authorization screen: установлен успешно");
        } catch (Exception e) {
            logger.fatal("Authorization screen: не удалось установить", e);
            throw new RuntimeException("Authorization screen: не удалось установить");
        }
    }

    private void setMainScreen() {
        try {
            replaceSceneContent(MAIN_VIEW_PATH);
            Platform.runLater(() -> {
                stage.setTitle(MAIN_VIEW_TITLE);
                stage.setResizable(true);
                stage.setWidth(MAIN_VIEW_WIDTH);
                stage.setHeight(MAIN_VIEW_HEIGHT);
            });

            logger.info("Main screen: установлен успешно");
        } catch (Exception e) {
            logger.fatal("Main screen: не удалось установить", e);
            throw new RuntimeException("Main screen: не удалось установить");
        }
    }

    public void setRegScreen() {
        try {
            replaceSceneContent(REGISTRATION_VIEW_PATH);

            Platform.runLater(() -> {
                stage.setTitle(REGISTRATION_VIEW_TITLE);
                stage.setResizable(false);
            });

            logger.info("Registration screen: установлен успешно");
        } catch (Exception e) {
            logger.fatal("Registration screen: не удалось установить", e);
            throw new RuntimeException("Registration screen: не удалось установить");
        }
    }

    private void replaceSceneContent(String fxml) throws Exception {

        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
        Parent page = loader.load();
        if (stage.getScene() == null) {
            stage.setScene(new Scene(page));
        } else {
            stage.getScene().setRoot(page);
        }
        screen = loader.getController();
        screen.setClientApp(this);
    }

    public void connect() {
        try {
            Socket socket = new Socket(DEFAULT_IP, DEFAULT_PORT);
            socketThread = new SocketThread(this, "ClientSide SocketThread: " + socket.getInetAddress(), socket);
            logger.info("Успешно создан сокет для соединения с сервером");
        } catch (IOException e) {
            logger.info("Не удалось установить соединение с сервером", e);
            Notifier.show(5d, "Ошибка соединения", "Не удалось установить соединение с сервером", Notifier.NotificationType.ERROR);

            if (screen instanceof AuthScreen) {
                ((AuthScreen) screen).unblock();
            } else if (screen instanceof RegistrationScreen) {
                ((RegistrationScreen) screen).unblock();
            }
        }
    }

    public void logOut() {
        disconnect(true);
        setAuthScreen();
    }

    private void disconnect(boolean notifyServer) {
        if (socketThread != null && socketThread.isAlive()) {
            if (notifyServer) {
                socketThread.send(new DisconnectMessage());
                logger.info("Серверу отправлен запрос на отключение");
            }
            socketThread.interrupt();
            logger.info("Соединение с сервером разорвано");
            if (!(screen instanceof AuthScreen)) {
                Platform.runLater(this::setAuthScreen);
            }
        }
    }

    //endregion

    public Stage getStage() {
        return stage;
    }


    //region SocketThread

    @Override
    public void onStartSocketThread(SocketThread socketThread) {
        logger.info("ClientSide SocketThread запушен");
    }

    @Override
    public void onStopSocketThread(SocketThread socketThread) {
        logger.info("ClientSide SocketThread остановлен");
    }

    @Override
    public void onReadySocketThread(SocketThread socketThread, Socket socket) {
        logger.info("ClientSide SocketThread готов к работе");

        if (screen instanceof AuthScreen) {
            socketThread.send(new AuthRequestMessage(((AuthScreen) screen).getLogin(), ((AuthScreen) screen).getPassword()));
            logger.info("Отправлен запрос авторизации на сервер");
        } else if (screen instanceof RegistrationScreen) {
            socketThread.send(new RegistrationRequestMessage(((RegistrationScreen) screen).getLogin(), ((RegistrationScreen) screen).getPassword()));
            logger.info("Отправлен запрос регистрации на сервер");
        }
    }

    @Override
    public synchronized void onReceiveMessageSocketThread(SocketThread socketThread, Socket socket, Message message) {
        logger.info("Получено сообщение с сервера");
        if (message.getType() == MessageType.AUTH_RESPONSE) {
            logger.info("Тип сообщения: AUTH_RESPONSE");
            handleAuthorizeResponse((AuthResponseMessage) message);
        } else if (message.getType() == MessageType.ERROR) {
            logger.info("Тип сообщения: ERROR");
            handleErrorMessage((ErrorMessage) message);
        } else if (message.getType() == MessageType.FILES_LIST_RESPONSE) {
            logger.info("Тип сообщения: FILES_LIST_RESPONSE");
            handleFilesList((FilesListResponse) message);
        } else if (message.getType() == MessageType.FILE) {
            logger.info("Тип сообщения: FILE");
            handleFileMessage((FileMessage) message);
        } else if (message.getType() == MessageType.REG_RESPONSE) {
            logger.info("Тип сообщения: REG_RESPONSE");
            handleRegistrationMessage((RegistrationResponseMessage) message);
        } else {
            logger.fatal("Необрабатываемый тип сообщения: " + message.getType());
            throw new RuntimeException();
        }
    }

    @Override
    public void onExceptionSocketThread(SocketThread socketThread, Socket socket, Exception e) {
        logger.fatal("Ошибка в ClientSide SocketThread: ", e);
        throw new RuntimeException();
    }
    //endregion

    //region MessageHandlers

    private void handleAuthorizeResponse(AuthResponseMessage message) {
        if (message.isAuthorized()) {
            logger.info("Клиент успешно авторизован на сервере");
            ((AuthScreen) screen).unblock();
            setMainScreen();
        } else {
            Notifier.show(5d, "Авторизация", message.getMessage(), Notifier.NotificationType.INFORMATION);
            logger.info("Запрос авторизации отклонен сервером");
            disconnect(false);
            ((AuthScreen) screen).unblock();
        }
    }

    private void handleErrorMessage(ErrorMessage message) {
        Notifier.show(5d, "Ошибка на сервере", message.getDescrition(), Notifier.NotificationType.ERROR);
        logger.error("Ошибка на сервере: " + message.getDescrition());
        if (message.isDisconnect()) {
            disconnect(false);
        }
    }

    private synchronized void handleFilesList(FilesListResponse message) {

        ArrayList<File> files = message.getFilesList();
        if (screen instanceof MainScreen) {
            MainScreen mainScreen = (MainScreen) screen;
            mainScreen.fillTable(files, message.isRoot());
            mainScreen.setProgressIndicatorActivity(false, "");
            mainScreen.unblockAllButtons();
        }
    }

    private void handleFileMessage(FileMessage message) {
        try {
            // TODO check file name collisions
            Path savePath = Paths.get(STORAGE_PATH);
            if (Files.notExists(savePath)) {
                Files.createDirectories(savePath);
            }
            Files.write(Paths.get(STORAGE_PATH + message.getName()), message.getBytes());

            if (screen instanceof MainScreen) {
                MainScreen mainScreen = (MainScreen) screen;
                mainScreen.unblockAllButtons();
                Platform.runLater(() -> mainScreen.setProgressIndicatorActivity(false, ""));
            }

        } catch (IOException e) {
            Notifier.show(5d, "Сохранение", "При сохранении файла возникла ошибка", Notifier.NotificationType.ERROR);
            logger.error("Ошибка сохранения файла", e);
        }
    }

    private void handleRegistrationMessage(RegistrationResponseMessage message) {
        if (message.isRegistered()) {
            Notifier.show(5d, "Регистрация", "Пользователь успешно зарегистрирован", Notifier.NotificationType.INFORMATION);
            Platform.runLater(this::setAuthScreen);
        } else {
            Notifier.show(5d, "Регистрация", message.getMessage(), Notifier.NotificationType.ERROR);
            ((RegistrationScreen) screen).unblock();
        }
    }
    //endregion

    public void send(Message message) {
        socketThread.send(message);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable e) {
        Notifier.show(10, "Ошибка приложения", "При работе приложения возникла ошибка. Приложение было остановлено.", Notifier.NotificationType.ERROR);
        logger.fatal("Ошибка клиентского приложения", e);
        System.exit(1);
    }
}
