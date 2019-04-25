package com.esb.plugin.designer.editor.properties;

import com.intellij.ui.JBColor;
import com.intellij.util.ui.JBUI;

import javax.swing.*;
import java.awt.*;

public class PropertyBox extends JPanel {

    private PropertyLabel label;
    private PropertyInput input;

    PropertyBox(String propertyName) {
        setBorder(JBUI.Borders.empty(5, 5, 0, 0));
        setBackground(new JBColor(new Color(20, 20, 0, 0), new Color(20, 10, 0, 0)));
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        setAlignmentX(Component.LEFT_ALIGNMENT);

        this.label = new PropertyLabel(propertyName);
        add(label, BorderLayout.WEST);

        this.input = new PropertyInput();
        add(input, BorderLayout.CENTER);
    }

    public void addListener(InputChangeListener listener) {
        this.input.addListener(listener);
    }
}
