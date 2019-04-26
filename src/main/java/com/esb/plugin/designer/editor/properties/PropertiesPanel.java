package com.esb.plugin.designer.editor.properties;

import com.esb.plugin.designer.editor.SelectListener;
import com.esb.plugin.designer.editor.component.ComponentDescriptor;
import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.drawable.Drawable;
import com.esb.plugin.designer.graph.manager.GraphChangeNotifier;
import com.intellij.openapi.module.Module;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBPanel;
import com.intellij.util.ui.JBUI;

import javax.swing.*;
import javax.swing.border.MatteBorder;

public class PropertiesPanel extends JBPanel implements SelectListener {

    private final Module module;

    public PropertiesPanel(Module module) {
        MatteBorder matteBorder = BorderFactory.createMatteBorder(0, 10, 0, 0, getBackground());
        setBorder(matteBorder);
        setBackground(JBColor.WHITE);
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        this.module = module;
    }

    @Override
    public void onSelect(FlowGraph graph, Drawable drawable) {
        ComponentDescriptor component = drawable.component();
        if (component == null) {
            return;
        }

        removeAll();

        add(createTitleLabel(component.getDisplayName()));
        component.getPropertiesNames()
                .forEach(propertyName -> {
                    PropertyBox panel = new PropertyBox(propertyName);
                    panel.addListener(newText -> {
                        component.setPropertyValue(propertyName, newText);
                        GraphChangeNotifier notifier = module.getMessageBus().syncPublisher(GraphChangeNotifier.TOPIC);
                        notifier.onChange(graph);
                    });
                    add(panel);
                });

        add(Box.createVerticalGlue());
        revalidate();
        repaint();
    }

    @Override
    public void onUnselect(FlowGraph graph, Drawable drawable) {
        removeAll();
        revalidate();
        repaint();
    }

    private JLabel createTitleLabel(String title) {
        JLabel titleLabel = new JLabel(title);
        titleLabel.setBorder(JBUI.Borders.empty(5, 5, 0, 0));
        return titleLabel;
    }

}
