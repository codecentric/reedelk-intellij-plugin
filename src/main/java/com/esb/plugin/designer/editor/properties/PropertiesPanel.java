package com.esb.plugin.designer.editor.properties;

import com.esb.plugin.designer.editor.SelectListener;
import com.esb.plugin.designer.editor.component.ComponentDescriptor;
import com.esb.plugin.designer.graph.drawable.Drawable;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBPanel;
import io.github.classgraph.MethodInfo;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.util.List;

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
        titleLabel.setBorder(new EmptyBorder(5, 5, 0, 0));
        add(titleLabel);

        List<MethodInfo> properties = component.getProperties();
        for (MethodInfo property : properties) {
            JPanel panel = new JPanel();
            panel.setBorder(new EmptyBorder(5, 5, 0, 0));
            panel.setBackground(new Color(0, 0, 0, 0));
            panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));

            JLabel label = new JLabel(property.getName().substring(3));
            label.setAlignmentX(Component.LEFT_ALIGNMENT);
            label.setBorder(new EmptyBorder(0, 0, 0, 5));
            JTextField input = new JTextField();
            input.setMaximumSize(new Dimension(300, 50));
            input.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(label, BorderLayout.WEST);
            panel.add(input, BorderLayout.CENTER);
            panel.setAlignmentX(Component.LEFT_ALIGNMENT);
            add(panel);
        }

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
