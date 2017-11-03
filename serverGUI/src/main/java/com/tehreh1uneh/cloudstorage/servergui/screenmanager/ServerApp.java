package com.tehreh1uneh.cloudstorage.servergui.screenmanager;

import com.tehreh1uneh.cloudstorage.common.notification.Notifier;
import com.tehreh1uneh.cloudstorage.server.Server;
import com.tehreh1uneh.cloudstorage.server.ServerListener;
import com.tehreh1uneh.cloudstorage.servergui.screens.mainscreen.MainScreen;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import static com.tehreh1uneh.cloudstorage.servergui.screenmanager.Config.*;

public class ServerApp extends Application implements ServerListener, Thread.UncaughtExceptionHandler {

    private static final Logger logger = Logger.getLogger(ServerApp.class);
    private Server server;
    private MainScreen controller;

    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loader = new FXMLLoader(getClass().getResource(MAIN_SCREEN_PATH));
        Parent root = loader.load();

        stage.setOnCloseRequest(windowEvent -> System.exit(0));
        stage.setTitle(TITLE);
        stage.setScene(new Scene(root));
        stage.show();

        controller = loader.getController();
        controller.setServerApp(this);
        server = new Server(this);
        Thread.setDefaultUncaughtExceptionHandler(this);

        logger.info("Серверное приложение стартовано");
    }

    public void turnOnServer() {
        server.turnOn(DEFAULT_PORT, TIMEOUT);
    }

    public void turnOffServer() {
        server.turnOff();
    }

    //region ServerListener

    @Override
    public void onConnect() {
        controller.blockButtonStart();
        Notifier.show(5d, "Сервер", "Сервер запущен", Notifier.NotificationType.INFORMATION);
    }

    @Override
    public void onDisconnect() {
        controller.blockButtonStop();
        Notifier.show(5d, "Сервер", "Сервер остановлен", Notifier.NotificationType.INFORMATION);
    }
    //endregion

    @Override
    public void uncaughtException(Thread thread, Throwable e) {
        Notifier.show(10, "Ошибка приложения", "При работе приложения возникла ошибка. Приложение было остановлено.", Notifier.NotificationType.ERROR);
        logger.fatal("Ошибка в потоке " + thread.getName(), e);
        System.exit(1);
    }
}
