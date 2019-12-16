package com.reedelk.plugin.commons;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationListener;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;

public class NotificationUtils {

    private static final String REEDELK_NOTIFICATION_GROUP_ID = "Reedelk Integration";

    private NotificationUtils() {
    }

    public static void notifyError(String title, String htmlContent) {
        Notification notification =
                new Notification(REEDELK_NOTIFICATION_GROUP_ID, null, NotificationType.ERROR)
                        .setTitle(title)
                        .setListener(NotificationListener.URL_OPENING_LISTENER)
                        .setContent(htmlContent);
        Notifications.Bus.notify(notification);
    }
}
