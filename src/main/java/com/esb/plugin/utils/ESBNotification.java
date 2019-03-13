package com.esb.plugin.utils;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;

public class ESBNotification {

    private static final String NOTIFICATION_GROUP = "ESB_NOTIFICATIONS";
    private static final String TITLE = "ESB";

    public static void notifyInfo(final String text, final Project project) {
        new Notification(NOTIFICATION_GROUP, TITLE, text, NotificationType.INFORMATION).notify(project);
    }

    public static void notifyError(Exception ex, final Project project) {
        new Notification(NOTIFICATION_GROUP, TITLE, StringUtil.notNullize(ex.getMessage(), "internal error"), NotificationType.ERROR).notify(project);
    }
}
