package com.tehreh1uneh.cloudstorage.servergui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ServerGUILauncher extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("servergui.fxml"));
        stage.setTitle("Cloud Storage [SERVER]");
        stage.setScene(new Scene(root, 600, 400));
        stage.show();
    }
}
