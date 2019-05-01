package com.esb.plugin.designer.properties.widget;

import com.esb.plugin.designer.properties.InputChangeListener;
import com.intellij.ui.components.JBPanel;
import com.intellij.util.ui.JBUI;

import javax.swing.*;
import java.awt.*;

public class PropertyBox extends JBPanel {

    private PropertyLabel label;
    private PropertyInput input;

    public PropertyBox(String propertyName) {
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        setAlignmentX(Component.LEFT_ALIGNMENT);

        setBorder(JBUI.Borders.empty(4, 4, 0, 0));

        label = new PropertyLabel(propertyName);
        add(label);

        input = new PropertyInput();
        add(input);
    }

    public void addListener(InputChangeListener listener) {
        input.addListener(listener);
    }

    public void setText(String data) {
        input.setText(data);
    }
}
