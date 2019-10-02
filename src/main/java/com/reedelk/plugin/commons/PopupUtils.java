package com.reedelk.plugin.commons;

import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.ui.awt.RelativePoint;

import javax.swing.*;

import static com.intellij.openapi.ui.MessageType.WARNING;
import static com.intellij.openapi.ui.popup.Balloon.Position;

public class PopupUtils {

    private PopupUtils() {
    }

    public static void error(Exception exception, JComponent component) {
        String errorMessage = exception.getMessage();
        String content = String.format(Labels.BALLOON_EDIT_CONFIG_ERROR, errorMessage);
        JBPopupFactory.getInstance().createHtmlTextBalloonBuilder(content, WARNING, null)
                .createBalloon()
                .show(RelativePoint.getCenterOf(component), Position.above);
    }
}
