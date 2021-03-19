package de.codecentric.reedelk.plugin.commons;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationListener;
import com.intellij.notification.Notifications;

import static com.intellij.notification.NotificationType.ERROR;
import static com.intellij.notification.NotificationType.INFORMATION;
import static de.codecentric.reedelk.plugin.message.ReedelkBundle.message;

public class NotificationUtils {

    private NotificationUtils() {
    }

    public static void notifyInfo(String title, String htmlContent) {
        Notification notification = new Notification(message("notification.group.id"), title, htmlContent, INFORMATION)
                .setListener(NotificationListener.URL_OPENING_LISTENER);
        Notifications.Bus.notify(notification);
    }

    public static void notifyError(String title, String htmlContent) {
        Notification notification = new Notification(message("notification.group.id"), title, htmlContent, ERROR)
                        .setListener(NotificationListener.URL_OPENING_LISTENER);
        Notifications.Bus.notify(notification);
    }
}
