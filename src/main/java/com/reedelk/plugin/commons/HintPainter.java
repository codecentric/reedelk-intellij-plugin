package com.reedelk.plugin.commons;

import com.intellij.ui.components.JBTextField;
import com.reedelk.runtime.api.commons.StringUtils;

import java.awt.*;

import static java.awt.RenderingHints.KEY_TEXT_ANTIALIASING;
import static java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_ON;

public class HintPainter {

    public static void from(Graphics g, JBTextField textField, String hint) {
        from(g, textField, hint, Colors.INPUT_FIELD_HINT);
    }

    public static void from(Graphics g, JBTextField textField, String hint, Color color) {
        // Hint is painted if and only if it is present
        if (StringUtils.isNotBlank(hint) && textField.getText().length() == 0) {
            int h = textField.getHeight();
            ((Graphics2D) g).setRenderingHint(KEY_TEXT_ANTIALIASING, VALUE_TEXT_ANTIALIAS_ON);
            Insets ins = textField.getInsets();
            FontMetrics fm = g.getFontMetrics();
            g.setColor(color);
            g.drawString(hint, ins.left + 7, h / 2 + fm.getAscent() / 2 - 2);
        }
    }
}
