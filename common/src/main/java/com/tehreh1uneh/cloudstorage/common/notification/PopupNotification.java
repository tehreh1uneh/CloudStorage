package com.tehreh1uneh.cloudstorage.common.notification;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Popup;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import java.io.IOException;

public class PopupNotification {

    Logger logger = Logger.getLogger(PopupNotification.class);

    public void showPopup(Stage stage, String message) {

        Platform.runLater(() -> {
            Popup popup = new Popup();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Notification.fxml"));

            Parent page = null;
            try {
                page = loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            popup.getContent().addAll(page);

            popup.centerOnScreen();
            ((Notification) loader.getController()).setMessage(message);


            popup.show(stage);

        });


    }
}
