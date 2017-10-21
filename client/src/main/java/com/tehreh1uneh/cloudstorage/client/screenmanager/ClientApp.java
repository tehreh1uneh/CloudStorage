package com.tehreh1uneh.cloudstorage.client.screenmanager;

import com.tehreh1uneh.cloudstorage.client.screens.BaseScreen;
import com.tehreh1uneh.cloudstorage.client.screens.authscreen.AuthScreen;
import com.tehreh1uneh.cloudstorage.client.screens.mainscreen.MainScreen;
import com.tehreh1uneh.cloudstorage.common.SocketThread;
import com.tehreh1uneh.cloudstorage.common.SocketThreadListener;
import com.tehreh1uneh.cloudstorage.common.messages.ErrorMessage;
import com.tehreh1uneh.cloudstorage.common.messages.auth.AuthReq;
import com.tehreh1uneh.cloudstorage.common.messages.auth.AuthResp;
import com.tehreh1uneh.cloudstorage.common.messages.base.Message;
import com.tehreh1uneh.cloudstorage.common.messages.base.MessageType;
import com.tehreh1uneh.cloudstorage.common.messages.files.FileResp;
import com.tehreh1uneh.cloudstorage.common.messages.files.FilesListResp;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import static com.tehreh1uneh.cloudstorage.client.screenmanager.Config.*;

// TODO create ?observers for screens

public class ClientApp extends Application implements SocketThreadListener, Thread.UncaughtExceptionHandler {

    private static final Logger logger = Logger.getLogger(ClientApp.class);

    //region Screens

    private Stage stage;
    private SocketThread socketThread;
    private BaseScreen screen;

    public Stage getStage() {
        return stage;
    }

    @Override
    public void start(Stage stage) throws Exception {
        Thread.setDefaultUncaughtExceptionHandler(this);
        this.stage = stage;
        setAuthScreen();
        logger.info("Клиентский GUI запущен");
    }

    private void setAuthScreen() {
        try {
            replaceSceneContent("/AuthScreen.fxml");

            stage.setOnCloseRequest(windowEvent -> {
                disconnect(true);
                System.exit(0);
            });

            stage.setTitle("Авторизация");
            stage.setResizable(false);
            stage.setWidth(350);
            stage.setHeight(450);
            stage.show();

            logger.info("Успешно установлен экран авторизации");
        } catch (Exception e) {
            logger.fatal("Не удалость установить экран авторизации", e);
            throw new RuntimeException("Не удалость установить экран авторизации");
        }
    }

    private void setMainScreen() {
        try {
            replaceSceneContent("/MainScreen.fxml");
            Platform.runLater(() -> {
                stage.setTitle("Cloud storage");
                stage.setResizable(true);
                stage.setWidth(800);
                stage.setHeight(600);
            });

            logger.info("Успешно установлен основной экран");
        } catch (Exception e) {
            logger.fatal("Не удалость установить основной экран", e);
            throw new RuntimeException("Не удалость установить основной экран");
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
            socketThread = new SocketThread(this, "SocketThread: " + socket.getInetAddress(), socket);
            logger.info("Успешно создан сокет для соединения с сервером");
        } catch (IOException e) {
            // TODO popup
            logger.info("Не удалось установить соединение с сервером", e);
            ((AuthScreen) screen).unblock();
        }
    }

    public void logOut() {
        disconnect(true);
        setAuthScreen();
    }

    private void disconnect(boolean notifyServer) {
        if (socketThread != null && socketThread.isAlive()) {
            if (notifyServer) {
                // TODO fix error
//                socketThread.send(new DisconnectMessage());
                logger.info("Серверу отправлен запрос на отключение");
            }
            socketThread.interrupt();
            logger.info("Соединение с сервером разорвано");
        }
    }

    //endregion

    //region SocketThread

    @Override
    public void onStartSocketThread(SocketThread socketThread) {
        logger.info("Клиентский SocketThread запушен");
    }

    @Override
    public void onStopSocketThread(SocketThread socketThread) {
        logger.info("Клиентский SocketThread  остановлен");
    }

    @Override
    public void onReadySocketThread(SocketThread socketThread, Socket socket) {
        logger.info("Клиентский SocketThread  готов к работе");
        AuthScreen authScreen = (AuthScreen) screen;
        socketThread.send(new AuthReq(authScreen.getLogin(), authScreen.getPassword()));
        logger.info("Отправлен запрос авторизации на сервер");
    }

    @Override
    public void onReceiveMessageSocketThread(SocketThread socketThread, Socket socket, Message message) {
        logger.info("Получено сообщение с сервера");
        if (message.getType() == MessageType.AUTH_RESP) {
            logger.info("Тип сообщения: AUTH_RESP");
            handleAuthorizeResponse((AuthResp) message);
        } else if (message.getType() == MessageType.ERROR) {
            logger.info("Тип сообщения: ERROR");
            handleErrorMessage((ErrorMessage) message);
        } else if (message.getType() == MessageType.FILES_LIST_RESP) {
            logger.info("Тип сообщения: FILES_LIST_RESP");
            handleFilesList((FilesListResp) message);
        } else if (message.getType() == MessageType.FILE_RESP) {
            logger.info("Тип сообщения: FILE_RESP");
            handleFileMessage((FileResp) message);
        } else {
            logger.fatal("Необрабатываемый тип сообщения: " + message.getType());
            throw new RuntimeException();
        }
    }

    @Override
    public void onExceptionSocketThread(SocketThread socketThread, Socket socket, Exception e) {
        logger.fatal("Ошибка в  SocketThread: ", e);
        throw new RuntimeException();
    }
    //endregion

    public void send(Message message) {
        socketThread.send(message);
    }

    //region MessageHandlers

    private void handleAuthorizeResponse(AuthResp message) {
        if (message.isAuthorized()) {
            logger.info("Клиент успешно авторизован на сервере");
            ((AuthScreen) screen).unblock();
            setMainScreen();
        } else {
            // TODO popup
            logger.info("Запрос авторизации отклонен сервером");
            disconnect(false);
            ((AuthScreen) screen).unblock();
        }
    }

    private void handleErrorMessage(ErrorMessage message) {
        // TODO info window
        logger.error("Ошибка на сервере: " + message.getDescrition());
        if (message.isDisconnect()) {
            disconnect(false);
        }
    }

    private void handleFilesList(FilesListResp message) {

        ArrayList<File> files = message.getFilesList();
        if (screen instanceof MainScreen) {
            MainScreen mainScreen = (MainScreen) screen;
            mainScreen.fillTable(files);
        }
    }

    private void handleFileMessage(FileResp message) {
        try {
            // TODO check file name collisions
            Path savePath = Paths.get(STORAGE_PATH);
            if (Files.notExists(savePath)) {
                Files.createDirectory(savePath);
            }
            Files.write(Paths.get(STORAGE_PATH + message.getName()), message.getBytes());
        } catch (IOException e) {
            // TODO popup
            logger.error("Ошибка сохранения файла", e);
        }
    }

    //endregion

    @Override
    public void uncaughtException(Thread thread, Throwable e) {
        logger.fatal("Ошибка клиентского приложения", e);
        JOptionPane.showMessageDialog(null, "Возникла непредвиденная ошибка, приложение будет закрыто.", "Ошибка:", JOptionPane.ERROR_MESSAGE);
        System.exit(1);
    }
}
