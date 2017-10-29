package com.tehreh1uneh.cloudstorage.client.screens.registrationscreen;

import com.tehreh1uneh.cloudstorage.client.screens.BaseScreen;
import com.tehreh1uneh.cloudstorage.common.notification.Notifier;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import org.apache.log4j.Logger;

import java.net.URL;
import java.util.ResourceBundle;

public final class RegistrationScreen extends BaseScreen implements Initializable {

    private static final Logger logger = Logger.getLogger(RegistrationScreen.class);

    //region View fields

    @FXML
    private ProgressIndicator progressIndicator;
    @FXML
    private TextField passwordConfirmation;
    @FXML
    private TextField password;
    @FXML
    private TextField login;
    //endregion

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        logger.info("Экран регистрации успешно инициализирован");
    }

    //region Getters

    public String getPassword() {
        return password.getText();
    }

    public String getLogin() {
        return login.getText();
    }
    //endregion

    //region Events

    @FXML
    private void onActionRegister() {

        boolean cancel = false;

        if (login.getText().isEmpty()) {
            Notifier.show(5d, "Регистрация", "Введите логин", Notifier.NotificationType.WARNING);
            cancel = true;
        }

        if (password.getText().isEmpty()) {
            Notifier.show(5d, "Регистрация", "Введите пароль", Notifier.NotificationType.WARNING);
            cancel = true;
        }

        if (!password.getText().equals(passwordConfirmation.getText()) && !password.getText().isEmpty()) {
            Notifier.show(5d, "Регистрация", "Подтверждение пароля не совпадает с паролем", Notifier.NotificationType.WARNING);
            cancel = true;
        }

        if (cancel) return;

        block();
        new Thread(() -> clientApp.connect()).start();
    }

    @FXML
    private void onActionCancel() {
        clientApp.setAuthScreen();
    }
    //endregion

    private void block() {
        logger.info("Интерфейс заблокирован");
        login.setEditable(false);
        password.setEditable(false);
        passwordConfirmation.setEditable(false);
        progressIndicator.setVisible(true);
    }

    public void unblock() {
        logger.info("Интерфейс разблокирован");
        login.setEditable(true);
        password.setEditable(true);
        passwordConfirmation.setEditable(true);
        progressIndicator.setVisible(false);
    }
}
