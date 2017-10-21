package com.tehreh1uneh.cloudstorage.common.notification;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class Notification implements Initializable {

    @FXML
    private Label messageLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    public void setMessage(String message) {
        messageLabel.setText(message);
    }

}
