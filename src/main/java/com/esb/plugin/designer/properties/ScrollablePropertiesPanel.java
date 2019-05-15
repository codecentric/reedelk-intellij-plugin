package com.esb.plugin.designer.properties;

import com.esb.plugin.designer.SelectListener;
import com.esb.plugin.graph.GraphSnapshot;
import com.esb.plugin.graph.node.GraphNode;
import com.intellij.ui.components.JBScrollPane;

public class ScrollablePropertiesPanel extends JBScrollPane implements SelectListener {

    private PropertiesPanel propertiesPanel;

    public ScrollablePropertiesPanel() {
        super();
        this.propertiesPanel = new PropertiesPanel();
        setViewportView(propertiesPanel);
        createVerticalScrollBar();
        setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_AS_NEEDED);
    }

    @Override
    public void onSelect(GraphSnapshot snapshot, GraphNode node) {
        propertiesPanel.onSelect(snapshot, node);
    }

    @Override
    public void onUnselect() {
        propertiesPanel.onUnselect();
    }
}
