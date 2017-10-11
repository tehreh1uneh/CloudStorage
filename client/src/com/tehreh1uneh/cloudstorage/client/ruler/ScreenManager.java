package com.tehreh1uneh.cloudstorage.client.ruler;

import com.tehreh1uneh.cloudstorage.client.screens.BaseScreen;
import com.tehreh1uneh.cloudstorage.client.screens.authscreen.AuthScreen;
import com.tehreh1uneh.cloudstorage.common.Messages.*;
import com.tehreh1uneh.cloudstorage.common.SocketThread;
import com.tehreh1uneh.cloudstorage.common.SocketThreadListener;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.IOException;
import java.net.Socket;

import static com.tehreh1uneh.cloudstorage.client.ruler.Config.DEFAULT_IP;
import static com.tehreh1uneh.cloudstorage.client.ruler.Config.DEFAULT_PORT;

public class ScreenManager extends Application implements SocketThreadListener, Thread.UncaughtExceptionHandler {

    private Stage stage;
    private SocketThread socketThread;
    private Converter converter;
    private BaseScreen screen;

    @Override
    public void start(Stage stage) throws Exception {

        Thread.setDefaultUncaughtExceptionHandler(this);
        converter = new Converter();
        this.stage = stage;

        setAuthScreen();
    }


    //region SocketThreadListener

    @Override
    public void onStartSocketThread(SocketThread socketThread) {
        System.out.println("Клиентский SocketThread запушен");
    }

    @Override
    public void onStopSocketThread(SocketThread socketThread) {
        System.out.println("Клиентский SocketThread  остановлен");
    }

    @Override
    public void onReadySocketThread(SocketThread socketThread, Socket socket) {
        System.out.println("Клиентский SocketThread  готов к работе");
        AuthScreen authScreen = (AuthScreen) screen;
        socketThread.send(converter.objectToBytes(new AuthRequestMessage(authScreen.getLogin(), authScreen.getPassword())));
    }

    @Override
    public void onReceiveMessageSocketThread(SocketThread socketThread, Socket socket, byte[] value) {
        Message message = converter.bytesToMessage(value);
        if (message.getType() == MessageType.AUTH_RESPONSE) {
            handleAuthorizeResponse((AuthResponseMessage) message);
        }
    }

    @Override
    public void onExceptionSocketThread(SocketThread socketThread, Socket socket, Exception e) {
        throw new RuntimeException("Ошибка в клиентском SocketThread");
    }
    //endregion

    public void connect() {
        try {
            Socket socket = new Socket(DEFAULT_IP, DEFAULT_PORT);
            socketThread = new SocketThread(this, "SocketThread: " + socket.getInetAddress(), socket);
        } catch (IOException e) {
            // TODO popup
            e.printStackTrace();
        }
    }

    private void disconnect() {
        socketThread.close();
    }

    private void handleAuthorizeResponse(AuthResponseMessage message) {
        if (message.isAuthorized()) {
            System.out.println("Успешное подключение к серверу");
            setMainScreen();
        } else {
            // TODO popup
            System.out.println("Неверный логин или пароль");
            disconnect();
        }
    }

    private void setAuthScreen() {
        try {
            replaceSceneContent("/com/tehreh1uneh/cloudstorage/client/screens/authscreen/AuthScreen.fxml");

            stage.setOnCloseRequest(windowEvent -> {
                disconnect();
                System.exit(0);
            });

            stage.setTitle("Авторизация");
            stage.setResizable(false);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Не удалость установить экран авторизации");
        }
    }

    private void setMainScreen() {
        try {
            replaceSceneContent("/com/tehreh1uneh/cloudstorage/client/screens/mainscreen/MainScreen.fxml");
            Platform.runLater(() -> {
                stage.setTitle("Cloud storage");
                stage.setResizable(true);
                stage.setWidth(800);
                stage.setHeight(600);
            });

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Не удалость установить основной экран");
        }
    }

    private void replaceSceneContent(String fxml) throws Exception {

        FXMLLoader loader = new FXMLLoader(ScreenManager.class.getResource(fxml));
        Parent page = loader.load();
        Scene scene = stage.getScene();
        if (scene == null) {
            scene = new Scene(page, 300, 400);
            stage.setScene(scene);
        } else {
            stage.getScene().setRoot(page);
        }
        screen = loader.getController();
        screen.setScreenManager(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        throwable.printStackTrace();
        StackTraceElement[] stackTraceElements = throwable.getStackTrace();
        String msg;

        if (stackTraceElements.length == 0) {
            msg = "Пустой stack trace";
        } else {
            msg = throwable.getClass().getCanonicalName() + ": " + throwable.getMessage() + "\n" + stackTraceElements[0];
        }

        JOptionPane.showMessageDialog(null, msg, "Ошибка:", JOptionPane.ERROR_MESSAGE);
        System.exit(1);
    }
}
