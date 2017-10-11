package com.tehreh1uneh.cloudstorage.servergui;

import com.tehreh1uneh.cloudstorage.server.LogListener;
import com.tehreh1uneh.cloudstorage.server.Server;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

import static com.tehreh1uneh.cloudstorage.servergui.Config.DEFAULT_PORT;
import static com.tehreh1uneh.cloudstorage.servergui.Config.TIMEOUT;

public final class ServerGUI implements Initializable, LogListener, Thread.UncaughtExceptionHandler {

    private Stage stage;
    private Server server;

    //region GUI_fields
    @FXML
    private Hyperlink sourceCodeLink;
    @FXML
    private TextArea logArea;
    //endregion

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Thread.setDefaultUncaughtExceptionHandler(this);
        server = new Server(this);
        log("Инициализация оболочки сервера успешно завершена");
    }

    //region Setters
    void setStage(Stage stage) {
        this.stage = stage;
    }
    //endregion

    //region GUI_events
    @FXML
    private void onClickStartServer() {
        server.turnOn(DEFAULT_PORT, TIMEOUT);
    }

    @FXML
    private void onClickStopServer() {
        server.turnOff();
    }

    @FXML
    private void openSourceCodeLink() {
        log("Source code link opened");
        String url = "https://github.com/tehreh1uneh/CloudStorage";

        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            try {
                desktop.browse(new URI(url));
                sourceCodeLink.setText("Source code");
            } catch (IOException | URISyntaxException e) {
                log(e.toString(), "Source code link: " + url);
            }
        } else {
            log("Source code link: " + url);
            sourceCodeLink.setText(url);
        }
    }
    //endregion

    @SuppressWarnings("ForLoopReplaceableByForEach")
    @Override
    public void log(String... msg) {
        for (int i = 0; i < msg.length; i++) logArea.appendText(msg[i] + "\n");
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        throwable.printStackTrace();
        StackTraceElement[] stackTraceElements = throwable.getStackTrace();
        String msg;

        if (stackTraceElements.length == 0) {
            msg = "Пустой stack trace";
        } else {
            msg = throwable.getClass().getCanonicalName() + ": " + throwable.getMessage() + "\n" + stackTraceElements[0];
        }

        JOptionPane.showMessageDialog(null, msg, "Ошибка:", JOptionPane.ERROR_MESSAGE);
        System.exit(1);
    }
}
