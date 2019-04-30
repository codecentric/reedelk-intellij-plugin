package com.esb.plugin.designer;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;

public interface SelectListener {

    void onSelect(FlowGraph graph, GraphNode node);

    void onUnselect(FlowGraph graph, GraphNode node);
}
