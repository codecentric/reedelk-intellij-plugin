package com.reedelk.plugin.commons;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.wm.ToolWindowId;
import com.intellij.openapi.wm.ToolWindowManager;

import javax.swing.*;

public class NotificationUtils {

    private NotificationUtils() {
    }

    public static void notifyInfo(final String toolWindowId, final String text, final Project project) {
        SwingUtilities.invokeLater(() -> ToolWindowManager.getInstance(project)
                .notifyByBalloon(toolWindowId, MessageType.INFO, text));

    }

    public static void notifyWarn(final String text, final Project project) {
        SwingUtilities.invokeLater(() -> ToolWindowManager.getInstance(project)
                .notifyByBalloon(ToolWindowId.RUN, MessageType.WARNING, text));
    }

    public static void notifyError(final String text, final Project project) {
        SwingUtilities.invokeLater(() -> ToolWindowManager.getInstance(project)
                .notifyByBalloon(ToolWindowId.RUN, MessageType.ERROR, StringUtil.notNullize(text, "internal error")));
    }

    public static void notifyError(final Exception ex, final Project project) {
        SwingUtilities.invokeLater(() -> ToolWindowManager.getInstance(project)
                .notifyByBalloon(ToolWindowId.RUN, MessageType.ERROR, StringUtil.notNullize(ex.getMessage(), "internal error")));
    }
}
