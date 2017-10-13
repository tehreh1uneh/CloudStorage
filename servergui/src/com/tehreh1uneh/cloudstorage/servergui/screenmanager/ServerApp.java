package com.tehreh1uneh.cloudstorage.servergui.screenmanager;

import com.tehreh1uneh.cloudstorage.server.LogListener;
import com.tehreh1uneh.cloudstorage.server.Server;
import com.tehreh1uneh.cloudstorage.servergui.screens.mainscreen.MainScreen;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static com.tehreh1uneh.cloudstorage.servergui.screenmanager.Config.DEFAULT_PORT;
import static com.tehreh1uneh.cloudstorage.servergui.screenmanager.Config.TIMEOUT;

public class ServerApp extends Application implements Thread.UncaughtExceptionHandler, LogListener {

    private Server server;
    private MainScreen controller;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/tehreh1uneh/cloudstorage/servergui/screens/mainscreen/MainScreen.fxml"));
        Parent root = loader.load();

        stage.setOnCloseRequest(windowEvent -> System.exit(0));
        stage.setTitle("Cloud Storage [SERVER]");
        stage.setScene(new Scene(root, 600, 400));
        stage.show();

        controller = loader.getController();
        controller.setServerApp(this);
        server = new Server(this);
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    public void turnOnServer() {
        server.turnOn(DEFAULT_PORT, TIMEOUT);
    }

    public void turnOffServer() {
        server.turnOff();
    }

    public void openSourceCodeLink() {
        String url = "https://github.com/tehreh1uneh/CloudStorage";

        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().browse(new URI(url));
                controller.sourceCodeLink.setText("Source code");
            } catch (IOException | URISyntaxException e) {
                log(e.toString(), "\nОшибка при открытии ссылки на исходный код");
            }
        } else {
            controller.sourceCodeLink.setText(url);
        }
    }

    @SuppressWarnings("ForLoopReplaceableByForEach")
    @Override
    public synchronized void log(String... msg) {
        controller.log(msg);
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
