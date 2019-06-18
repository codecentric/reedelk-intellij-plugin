package com.esb.plugin.editor.properties.widget;

import com.esb.plugin.component.domain.ComponentData;
import com.esb.plugin.editor.properties.renderer.node.NodePropertiesRendererFactory;
import com.esb.plugin.graph.FlowSnapshot;
import com.esb.plugin.graph.node.GraphNode;
import com.intellij.openapi.module.Module;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.JBUI;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import java.awt.*;

import static java.awt.BorderLayout.*;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS;

public class ContainerFactory {

    public static JBPanel createObjectTypeContainer(String title, JComponent renderedComponent) {
        JBPanel container = new JBPanel(new BorderLayout());
        Border outsideMargin = JBUI.Borders.emptyTop(15);
        Border border = BorderFactory.createTitledBorder(title);
        Border outside = new CompoundBorder(outsideMargin, border);
        Border margin = JBUI.Borders.empty(10);
        container.setBorder(new CompoundBorder(outside, margin));
        container.add(renderedComponent, CENTER);
        container.add(Box.createHorizontalGlue(), EAST);
        return container;
    }

    static JBScrollPane createPropertiesPanel(Module module, ComponentData componentData, FlowSnapshot snapshot, GraphNode node) {
        JBPanel propertiesPanel = NodePropertiesRendererFactory.get()
                .component(componentData)
                .snapshot(snapshot)
                .module(module)
                .build()
                .render(node);
        JBPanel propertiesBoxContainer = createPropertiesBoxPanel(propertiesPanel);
        JBPanel propertiesHolder = createPropertiesHolder(propertiesBoxContainer);
        return wrapInsideScrollPane(propertiesHolder);
    }

    static JBPanel createPropertiesBoxPanel(JBPanel propertiesListPanel) {
        JBPanel fillerPanel = new JBPanel();
        fillerPanel.add(Box.createGlue());

        JBPanel propertiesBoxContainer = new JBPanel();
        propertiesBoxContainer.setLayout(new BorderLayout());
        propertiesBoxContainer.add(propertiesListPanel, NORTH);
        propertiesBoxContainer.add(fillerPanel, CENTER);
        return propertiesBoxContainer;
    }

    private static JBPanel createPropertiesHolder(JBPanel propertiesBoxContainer) {
        JBPanel propertiesHolder = new JBPanel();
        propertiesHolder.setLayout(new BorderLayout());
        propertiesHolder.add(propertiesBoxContainer, CENTER);
        return propertiesHolder;
    }

    private static JBScrollPane wrapInsideScrollPane(JBPanel propertiesPanel) {
        JBScrollPane scrollPane = new JBScrollPane();
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setViewportView(propertiesPanel);
        scrollPane.createVerticalScrollBar();
        scrollPane.setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_ALWAYS);
        return scrollPane;
    }
}
