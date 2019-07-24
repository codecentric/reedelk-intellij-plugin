package com.esb.plugin.editor.properties.widget;

import com.esb.plugin.component.domain.ComponentData;
import com.esb.plugin.graph.FlowSnapshot;
import com.esb.plugin.graph.node.GraphNode;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.module.Module;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTabbedPane;

import javax.swing.*;

public class PropertiesTabbedPane extends JBTabbedPane implements Disposable {

    private final Module module;
    private final GraphNode node;
    private final FlowSnapshot snapshot;

    public PropertiesTabbedPane(GraphNode node, Module module, FlowSnapshot snapshot) {
        super();
        this.node = node;
        this.module = module;
        this.snapshot = snapshot;
        initialize();
    }

    @Override
    public void dispose() {
        // TODO: Complete
    }

    private void initialize() {
        ComponentData componentData = node.componentData();
        JBScrollPane propertiesPanel = ContainerFactory
                .createPropertiesPanel(module, componentData, snapshot, node);
        Icon icon = componentData.getComponentIcon();
        addTab(componentData.getDisplayName(), icon, propertiesPanel, componentData.getDisplayName() + " properties");
    }

}
