package com.tehreh1uneh.cloudstorage.client.screens.mainscreen;

import com.tehreh1uneh.cloudstorage.client.screens.BaseScreen;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

public final class MainScreen extends BaseScreen implements Initializable {

    @FXML
    private void onActionSourceCode() {

        String url = "https://github.com/tehreh1uneh/CloudStorage";
        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            try {
                desktop.browse(new URI(url));
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void onActionExit() {
        System.exit(0);
    }

    @FXML
    private void onActionSync() {
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    @FXML
    private void onActionLogOut() {
        screenManager.logOut();
    }
}
