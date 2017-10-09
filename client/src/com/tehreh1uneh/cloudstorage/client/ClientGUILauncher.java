package com.tehreh1uneh.cloudstorage.client;

import com.tehreh1uneh.cloudstorage.client.authscreen.AuthScreen;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public final class ClientGUILauncher extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("authscreen/AuthScreen.fxml"));
        Parent root = loader.load();

        AuthScreen authScreen = loader.getController();
        authScreen.setStage(stage);

        stage.setOnCloseRequest(windowEvent -> System.exit(0));
        stage.setTitle("Авторизация");
        stage.setScene(new Scene(root, 300, 400));
        stage.setResizable(false);
        stage.show();
    }
}
