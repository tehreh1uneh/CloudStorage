package com.tehreh1uneh.cloudstorage.servergui.screenmanager;

import com.tehreh1uneh.cloudstorage.server.Server;
import com.tehreh1uneh.cloudstorage.servergui.screens.mainscreen.MainScreen;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import javax.swing.*;

import static com.tehreh1uneh.cloudstorage.servergui.screenmanager.Config.DEFAULT_PORT;
import static com.tehreh1uneh.cloudstorage.servergui.screenmanager.Config.TIMEOUT;

public class ServerApp extends Application implements Thread.UncaughtExceptionHandler{

    private static final Logger logger = Logger.getLogger(ServerApp.class);
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
        server = new Server();
        Thread.setDefaultUncaughtExceptionHandler(this);
        logger.info("Серверное приложение стартовано");
    }

    public void turnOnServer() {
        server.turnOn(DEFAULT_PORT, TIMEOUT);
    }

    public void turnOffServer() {
        server.turnOff();
    }

    @Override
    public void uncaughtException(Thread thread, Throwable e) {
        logger.fatal("Ошибка в потоке " + thread.getName(), e);
        JOptionPane.showMessageDialog(null, "Возникла непредвиденная ошибка, приложение будет закрыто.", "Ошибка:", JOptionPane.ERROR_MESSAGE);
        System.exit(1);
    }
}
