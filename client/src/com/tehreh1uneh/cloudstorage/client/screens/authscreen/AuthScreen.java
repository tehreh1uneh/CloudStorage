package com.tehreh1uneh.cloudstorage.client.screens.authscreen;

import com.tehreh1uneh.cloudstorage.client.screens.BaseScreen;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.net.URL;
import java.util.ResourceBundle;

public final class AuthScreen extends BaseScreen implements Initializable {

    @FXML
    private TextField login;
    @FXML
    private PasswordField password;
    @FXML
    private ProgressIndicator progressIndicator;
    private boolean blocked;

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
        connect();
    }

    @FXML
    private void onKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            connect();
        }
    }

    private void connect() {
        if (!blocked) {
            block();
            new Thread(() -> clientApp.connect()).start();

        }
    }

    private void block() {
        blocked = true;
        login.setEditable(false);
        password.setEditable(false);
        progressIndicator.setVisible(true);
    }

    public void unblock() {
        blocked = false;
        login.setEditable(true);
        password.setEditable(true);
        progressIndicator.setVisible(false);

    }
}
