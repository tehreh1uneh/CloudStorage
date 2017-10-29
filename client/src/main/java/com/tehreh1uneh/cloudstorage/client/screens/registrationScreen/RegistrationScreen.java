package com.tehreh1uneh.cloudstorage.client.screens.registrationScreen;

import com.tehreh1uneh.cloudstorage.client.screens.BaseScreen;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import org.apache.log4j.Logger;

import java.net.URL;
import java.util.ResourceBundle;

public class RegistrationScreen extends BaseScreen implements Initializable {

    private static final Logger logger = Logger.getLogger(RegistrationScreen.class);
    private boolean blocked;

    @FXML
    private ProgressIndicator progressIndicator;
    @FXML
    private TextField passwordConfirmation;
    @FXML

    private TextField password;
    @FXML
    private TextField login;

    public String getPassword() {
        return password.getText();
    }

    public String getLogin() {
        return login.getText();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        logger.info("Экран регистрации успешно инициализирован");
    }

    @FXML
    private void onActionRegister(ActionEvent actionEvent) {
        if (password.getText().equals(passwordConfirmation.getText()) && !login.getText().isEmpty() && !password.getText().isEmpty()) {
            block();
            new Thread(() -> clientApp.connect()).start();
        } else if (!password.getText().equals(passwordConfirmation.getText())) {
            password.clear();
            passwordConfirmation.clear();
        }
    }

    private void block() {
        logger.info("Интерфейс заблокирован");
        blocked = true;
        login.setEditable(false);
        password.setEditable(false);
        passwordConfirmation.setEditable(false);
        progressIndicator.setVisible(true);
    }

    public void unblock() {
        logger.info("Интерфейс разблокирован");
        blocked = false;
        login.setEditable(true);
        password.setEditable(true);
        passwordConfirmation.setEditable(true);
        progressIndicator.setVisible(false);
    }

    @FXML
    private void onActionCancel(ActionEvent actionEvent) {
        // TODO set auth screen
    }
}
