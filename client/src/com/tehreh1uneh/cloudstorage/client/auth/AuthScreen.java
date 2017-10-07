package com.tehreh1uneh.cloudstorage.client.auth;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public final class AuthScreen {

    private Stage stage;

    @FXML
    private TextField login;

    @FXML
    private PasswordField password;

    @FXML
    private void mouseClickedAuth() {
        System.out.println("Button authorize clicked!");
        System.out.println("login: " + login.getText());
        System.out.println("pass: " + password.getText());
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
