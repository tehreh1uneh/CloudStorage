package com.tehreh1uneh.cloudstorage.client.screens.authscreen;

import com.tehreh1uneh.cloudstorage.client.screens.BaseScreen;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public final class AuthScreen extends BaseScreen implements Initializable {

    @FXML
    private TextField login;
    @FXML
    private PasswordField password;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("Инициализация экрана авторизации");
    }

    public String getLogin() {
        return login.getText();
    }

    public String getPassword() {
        return password.getText();
    }

    @FXML
    private void mouseClickedAuth() {
        screenManager.connect();
    }

}
