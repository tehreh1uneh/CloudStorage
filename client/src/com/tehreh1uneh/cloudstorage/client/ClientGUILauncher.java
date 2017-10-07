package com.tehreh1uneh.cloudstorage.client;

import com.tehreh1uneh.cloudstorage.client.auth.AuthScreen;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public final class ClientGUILauncher extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("auth/AuthScreen.fxml"));
        Parent root = loader.load();

        AuthScreen authScreen = loader.getController();
        authScreen.setStage(stage);

        stage.setTitle("Authorization");
        stage.setScene(new Scene(root, 300, 500));
        stage.setResizable(false);
        stage.show();
    }
}
