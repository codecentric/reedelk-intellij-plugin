package com.esb.plugin.designer.properties;

import com.esb.plugin.designer.SelectListener;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.GraphNode;
import com.intellij.ui.components.JBScrollPane;

public class ScrollablePropertiesPanel extends JBScrollPane implements SelectListener {

    private PropertiesPanel propertiesPanel;

    public ScrollablePropertiesPanel(PropertiesPanel propertiesPanel) {
        super(propertiesPanel);
        this.propertiesPanel = propertiesPanel;
        createVerticalScrollBar();
        setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_AS_NEEDED);
    }

    @Override
    public void onSelect(FlowGraph graph, GraphNode node) {
        propertiesPanel.onSelect(graph, node);
    }

    @Override
    public void onUnselect(FlowGraph graph, GraphNode node) {
        propertiesPanel.onUnselect(graph, node);
    }
}
