package com.tehreh1uneh.cloudstorage.client.screens.authscreen;

import com.tehreh1uneh.cloudstorage.client.screens.BaseScreen;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.apache.log4j.Logger;

import java.net.URL;
import java.util.ResourceBundle;

public final class AuthScreen extends BaseScreen implements Initializable {

    private static final Logger logger = Logger.getLogger(AuthScreen.class);

    @FXML
    private TextField login;
    @FXML
    private PasswordField password;
    @FXML
    private ProgressIndicator progressIndicator;
    private boolean blocked;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        logger.info("Экран авторизации успешно инициализирован");
    }

    public String getLogin() {
        return login.getText();
    }

    public String getPassword() {
        return password.getText();
    }

    private void connect() {
        if (!blocked) {
            logger.info("Интерфейс заблокирован. Попытка подлючения к серверу");
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
        logger.info("Интерфейс разблокирован");
        blocked = false;
        login.setEditable(true);
        password.setEditable(true);
        progressIndicator.setVisible(false);
    }

    @FXML
    private void onActionAuth(ActionEvent actionEvent) {
        connect();
    }
}
