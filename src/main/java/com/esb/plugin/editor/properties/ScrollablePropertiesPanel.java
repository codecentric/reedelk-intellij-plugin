package com.esb.plugin.editor.properties;

import com.esb.plugin.editor.designer.SelectListener;
import com.esb.plugin.graph.FlowSnapshot;
import com.esb.plugin.graph.node.GraphNode;
import com.intellij.openapi.module.Module;
import com.intellij.ui.components.JBScrollPane;

public class ScrollablePropertiesPanel extends JBScrollPane implements SelectListener {

    private PropertiesPanel propertiesPanel;

    public ScrollablePropertiesPanel(Module module, FlowSnapshot snapshot) {
        super();
        this.propertiesPanel = new PropertiesPanel(module, snapshot);
        setViewportView(propertiesPanel);
        createVerticalScrollBar();
        setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_AS_NEEDED);
    }

    @Override
    public void onSelect(FlowSnapshot snapshot, GraphNode node) {
        propertiesPanel.onSelect(snapshot, node);
    }

    @Override
    public void onUnselect() {
        propertiesPanel.onUnselect();
    }
}
