package com.tehreh1uneh.cloudstorage.common.notification;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

public final class Notifier {

    private Notifier() {
    }

    public synchronized static void show(double duration, String title, String text, NotificationType type) {
        Platform.runLater(() -> {
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
        });
    }

    public enum NotificationType {
        CONFIRM, ERROR, INFORMATION, WARNING
    }
}