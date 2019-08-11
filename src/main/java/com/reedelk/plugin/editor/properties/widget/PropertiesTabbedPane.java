package com.reedelk.plugin.editor.properties.widget;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.module.Module;
import com.intellij.ui.components.JBTabbedPane;
import com.reedelk.plugin.component.domain.ComponentData;
import com.reedelk.plugin.graph.FlowSnapshot;
import com.reedelk.plugin.graph.node.GraphNode;

import javax.swing.*;

public class PropertiesTabbedPane extends JBTabbedPane implements Disposable {

    private final Module module;
    private final GraphNode node;
    private final FlowSnapshot snapshot;

    private DisposableScrollPane propertiesScrollPane;

    public PropertiesTabbedPane(GraphNode node, Module module, FlowSnapshot snapshot) {
        super();
        this.node = node;
        this.module = module;
        this.snapshot = snapshot;
        initialize();
    }

    @Override
    public void dispose() {
        propertiesScrollPane.dispose();
    }

    private void initialize() {
        ComponentData componentData = node.componentData();

        propertiesScrollPane =
                ContainerFactory.createPropertiesPanel(module, componentData, snapshot, node);


        Icon icon = componentData.getComponentIcon();
        String displayName = componentData.getDisplayName();
        addTab(displayName,
                icon,
                propertiesScrollPane,
                displayName + " properties");
    }
}
