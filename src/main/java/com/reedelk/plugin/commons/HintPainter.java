package com.reedelk.plugin.commons;

import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBTextField;
import com.reedelk.runtime.api.commons.StringUtils;

import java.awt.*;

import static java.awt.RenderingHints.KEY_TEXT_ANTIALIASING;
import static java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_ON;

public class HintPainter {

    private static final Color HINT_COLOR = JBColor.LIGHT_GRAY;


    public static void from(Graphics g, JBTextField textField, String hint) {
        // Hint is painted if and only if it is present
        if (StringUtils.isNotBlank(hint) && textField.getText().length() == 0) {
            int h = textField.getHeight();
            ((Graphics2D) g).setRenderingHint(KEY_TEXT_ANTIALIASING, VALUE_TEXT_ANTIALIAS_ON);
            Insets ins = textField.getInsets();
            FontMetrics fm = g.getFontMetrics();
            g.setColor(HINT_COLOR);
            g.drawString(hint, ins.left + 5, h / 2 + fm.getAscent() / 2 - 2);
        }
    }
}
