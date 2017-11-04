package com.tehreh1uneh.cloudstorage.common.notification;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.stage.PopupWindow;
import javafx.stage.Window;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

import java.util.List;

public final class Notifier {

    private Notifier() {
    }

    public synchronized static void show(double duration, String title, String text, NotificationType type) {
        if (Platform.isFxApplicationThread()) {
            showNotification(duration, title, text, type);
        } else {
            Platform.runLater(() -> showNotification(duration, title, text, type));
        }
    }


    private synchronized static void focusAnyWindow() {
        List<Window> windows = Window.getWindows();
        if (windows == null || windows.isEmpty()) return;

        for (Window window : windows) {
            if (window.isFocused() && !(window instanceof PopupWindow)) {
                window.requestFocus();
                return;
            }
        }
        windows.get(0).requestFocus();
    }

    private synchronized static void showNotification(double duration, String title, String text, NotificationType type) {
        focusAnyWindow();

        Notifications newNotification = Notifications.create()
                .title(title)
                .text(text)
                .hideAfter(Duration.seconds(duration))
                .position(Pos.BOTTOM_RIGHT)
                .graphic(null);

        switch (type) {
            case CONFIRM:
                newNotification.showConfirm();
                break;
            case INFORMATION:
                newNotification.showInformation();
                break;
            case WARNING:
                newNotification.showWarning();
                break;
            case ERROR:
                newNotification.showError();
                break;
        }
    }

    public enum NotificationType {
        CONFIRM, ERROR, INFORMATION, WARNING
    }
}