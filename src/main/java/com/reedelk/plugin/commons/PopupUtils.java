package com.reedelk.plugin.commons;

import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.ui.awt.RelativePoint;
import com.reedelk.plugin.message.ReedelkBundle;

import javax.swing.*;

import static com.intellij.openapi.ui.MessageType.ERROR;
import static com.intellij.openapi.ui.MessageType.INFO;
import static com.intellij.openapi.ui.popup.Balloon.Position;

public class PopupUtils {

    private PopupUtils() {
    }

    public static void error(Exception exception, JComponent component) {
        String content = ReedelkBundle.message("popup.error.message", exception.getMessage());
        JBPopupFactory.getInstance().createHtmlTextBalloonBuilder(content, ERROR, null)
                .createBalloon()
                .show(RelativePoint.getNorthWestOf(component), Position.above);
    }

    public static void info(String text, JComponent component) {
        JBPopupFactory.getInstance()
                .createHtmlTextBalloonBuilder(text, INFO, HyperlinkListenerUtils.BROWSER_OPEN)
                .createBalloon()
                .show(RelativePoint.getCenterOf(component), Position.above);
    }
}
