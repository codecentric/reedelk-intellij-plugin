package com.reedelk.plugin.editor.properties.widget;

import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBPanel;
import com.intellij.util.ui.JBUI;

import javax.swing.*;
import java.awt.*;

public class HorizontalSeparator extends JBPanel {

    public HorizontalSeparator(JBColor color) {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JSeparator jSeparator = new JSeparator();
        jSeparator.setForeground(color);
        add(jSeparator, gbc);

        setBorder(JBUI.Borders.empty(2, 5, 0, 0));
    }
}
