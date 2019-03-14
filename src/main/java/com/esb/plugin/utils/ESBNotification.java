package com.esb.plugin.utils;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.ui.awt.RelativePoint;

public class ESBNotification {

    private static final String NOTIFICATION_GROUP = "ESB_NOTIFICATIONS";
    private static final String TITLE = "ESB";

    public static void notifyInfo(final String text, final Project project) {
       // new Notification(NOTIFICATION_GROUP, TITLE, text, NotificationType.INFORMATION).notify(project);
        final StatusBar statusBar = WindowManager.getInstance().getStatusBar(project);

        JBPopupFactory.getInstance()
                .createHtmlTextBalloonBuilder(text, MessageType.INFO,null)
                .setFadeoutTime(2000)
                .createBalloon()
                .show(RelativePoint.getNorthEastOf(statusBar.getComponent()), Balloon.Position.above);
    }

    public static void notifyError(Exception ex, final Project project) {
        new Notification(NOTIFICATION_GROUP, TITLE, StringUtil.notNullize(ex.getMessage(), "internal error"), NotificationType.ERROR).notify(project);
    }
}
