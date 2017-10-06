package com.tehreh1uneh.cloudstorage.servergui;

import javafx.fxml.Initializable;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextArea;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

public class ServerGUI implements Initializable {

    public Hyperlink sourceCodeLink;
    public TextArea logArea;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("Server GUI initialisation");
    }

    public void openSourceCodeLink() {

        String url = "https://github.com/tehreh1uneh/CloudStorage";

        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            try {
                desktop.browse(new URI(url));
                sourceCodeLink.setText("Source code");
            } catch (IOException | URISyntaxException e) {
                logArea.appendText(e.toString());
                logArea.appendText("\nSource code link: " + url);
            }
        } else {
            logArea.appendText("\nSource code link: " + url);
            sourceCodeLink.setText(url);
        }
    }
}
