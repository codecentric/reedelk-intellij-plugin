package com.esb.plugin.designer.editor.properties;

import com.esb.plugin.designer.editor.SelectListener;
import com.esb.plugin.designer.editor.component.ComponentDescriptor;
import com.esb.plugin.designer.graph.drawable.Drawable;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBPanel;
import com.intellij.util.ui.JBUI;

import javax.swing.*;
import javax.swing.border.MatteBorder;

public class PropertiesPanel extends JBPanel implements SelectListener {

    public PropertiesPanel() {
        MatteBorder matteBorder = BorderFactory.createMatteBorder(0, 10, 0, 0, getBackground());
        setBorder(matteBorder);
        setBackground(JBColor.WHITE);
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
    }

    @Override
    public void onSelect(Drawable drawable) {
        ComponentDescriptor component = drawable.component();
        if (component == null) return;

        removeAll();

        JLabel titleLabel = new JLabel(component.getDisplayName());
        titleLabel.setBorder(JBUI.Borders.empty(5, 5, 0, 0));
        add(titleLabel);

        component.getPropertiesNames()
                .forEach(propertyName -> {
                    PropertyBox panel = new PropertyBox(propertyName);
                    panel.addListener(newText ->
                            component.setPropertyValue(propertyName, newText));
                    add(panel);
                });

        add(Box.createVerticalGlue());
        revalidate();
        repaint();
    }

    @Override
    public void onUnselect(Drawable drawable) {
        removeAll();
        revalidate();
        repaint();
    }

}
