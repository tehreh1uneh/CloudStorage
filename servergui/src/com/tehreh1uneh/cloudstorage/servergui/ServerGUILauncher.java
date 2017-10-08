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

        FXMLLoader loader = new FXMLLoader(getClass().getResource("ServerGUI.fxml"));
        Parent root = loader.load();

        ServerGUI serverGUI = loader.getController();
        serverGUI.setStage(stage);

        stage.setOnCloseRequest(windowEvent -> System.exit(0));
        stage.setTitle("Cloud Storage [SERVER]");
        stage.setScene(new Scene(root, 600, 400));
        stage.show();
    }
}
