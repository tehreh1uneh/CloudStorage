package com.tehreh1uneh.cloudstorage.client.authscreen;

import com.tehreh1uneh.cloudstorage.common.Messages.*;
import com.tehreh1uneh.cloudstorage.common.SocketThread;
import com.tehreh1uneh.cloudstorage.common.SocketThreadListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

import static com.tehreh1uneh.cloudstorage.client.Config.DEFAULT_IP;
import static com.tehreh1uneh.cloudstorage.client.Config.DEFAULT_PORT;

public final class AuthScreen implements Initializable, SocketThreadListener, Thread.UncaughtExceptionHandler {

    private Stage stage;
    private SocketThread socketThread;
    private Converter converter;

    //region GUI_fields
    @FXML
    private TextField login;
    @FXML
    private PasswordField password;

    //endregion
    //region GUI_events
    @FXML
    private void mouseClickedAuth() {
        connect();
    }
    //endregion

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Thread.setDefaultUncaughtExceptionHandler(this);
        converter = new Converter();
        System.out.println("Инициализация экрана авторизации");
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private void connect() {
        try {
            Socket socket = new Socket(DEFAULT_IP, DEFAULT_PORT);
            socketThread = new SocketThread(this, "SocketThread", socket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void disconnect() {
        socketThread.close();
    }

    //region SocketThreadListenerMethods
    @Override
    public void onStartSocketThread(SocketThread socketThread) {
        System.out.println("Поток сокета запушен");
    }

    @Override
    public void onStopSocketThread(SocketThread socketThread) {
        System.out.println("Поток сокета остановлен");
    }

    @Override
    public void onReadySocketThread(SocketThread socketThread, Socket socket) {
        System.out.println("Поток сокета готов к работе");
        socketThread.send(converter.objectToBytes(new AuthRequestMessage(login.getText(), password.getText())));
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
        throw new RuntimeException("Ошибка в потоке сокета");
    }
    //endregion

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        throwable.printStackTrace();
        StackTraceElement[] stackTraceElements = throwable.getStackTrace();
        String msg;

        if (stackTraceElements.length == 0) {
            msg = "Пустой stack trace";
        } else {
            msg = new StringBuilder().append(throwable.getClass().getCanonicalName()).append(": ").append(throwable.getMessage()).append("\n").append(stackTraceElements[0]).toString();
        }

        JOptionPane.showMessageDialog(null, msg, "Ошибка:", JOptionPane.ERROR_MESSAGE);
        System.exit(1);
    }

    private void handleAuthorizeResponse(AuthResponseMessage message) {
        if (message.isAuthorized()) {
            // TODO change scene
            System.out.println("Успешное подключение к серверу");
        } else {
            System.out.println("Неверный логин или пароль");
            disconnect();
        }
    }

}
