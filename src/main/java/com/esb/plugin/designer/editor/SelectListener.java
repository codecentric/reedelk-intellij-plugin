package com.esb.plugin.designer.editor;

import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.GraphNode;

public interface SelectListener {

    void onSelect(FlowGraph graph, GraphNode drawable);

    void onUnselect(FlowGraph graph, GraphNode drawable);
}
