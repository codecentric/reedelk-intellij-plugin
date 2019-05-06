package com.esb.plugin.designer;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.manager.GraphChangeListener;
import com.esb.plugin.graph.node.GraphNode;

public interface SelectListener {

    void onSelect(FlowGraph graph, GraphNode node, GraphChangeListener listener);

    void onUnselect();

}
