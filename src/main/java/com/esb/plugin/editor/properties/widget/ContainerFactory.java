package com.esb.plugin.editor.properties.widget;

import com.esb.plugin.component.domain.ComponentData;
import com.esb.plugin.editor.properties.renderer.node.NodePropertiesRendererFactory;
import com.esb.plugin.graph.FlowSnapshot;
import com.esb.plugin.graph.node.GraphNode;
import com.intellij.openapi.module.Module;
import com.intellij.ui.components.JBPanel;

import javax.swing.*;
import java.awt.*;

import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.NORTH;

class ContainerFactory {

    static JBPanel createPropertiesPanel(Module module, ComponentData componentData, FlowSnapshot snapshot, GraphNode node) {
        JBPanel propertiesPanel = NodePropertiesRendererFactory.get()
                .component(componentData)
                .snapshot(snapshot)
                .module(module)
                .build()
                .render(node);
        JBPanel propertiesBoxContainer = createPropertiesBoxPanel(propertiesPanel);
        return createPropertiesHolder(propertiesBoxContainer);
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
}
