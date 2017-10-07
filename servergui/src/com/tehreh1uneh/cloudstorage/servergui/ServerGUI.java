package com.tehreh1uneh.cloudstorage.servergui;

import com.tehreh1uneh.cloudstorage.server.Server;
import com.tehreh1uneh.cloudstorage.server.ServerListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

public final class ServerGUI implements Initializable, ServerListener {

    //region GUI_fields
    @FXML
    private Hyperlink sourceCodeLink;
    @FXML
    private TextArea logArea;
    //endregion

    private Stage stage;
    private Server server;

    void setStage(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        server = new Server(this);
        logArea.appendText("Server GUI initialisation completed\n");
    }


    //region GUI_events
    @FXML
    private void onClickStartServer() {
        server.startServer(8189);
    }

    @FXML
    private void onClickStopServer() {
        logArea.appendText("Server has been stopped\n");
    }

    @FXML
    private void openSourceCodeLink() {
        logArea.appendText("Source code link opened\n");
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
    //endregion


    @Override
    public void log(Server server, String msg) {
        logArea.appendText(msg + "\n");
    }

}
