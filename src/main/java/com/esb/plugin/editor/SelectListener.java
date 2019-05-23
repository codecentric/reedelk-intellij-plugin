package com.esb.plugin.editor;

import com.esb.plugin.graph.GraphSnapshot;
import com.esb.plugin.graph.node.GraphNode;

public interface SelectListener {

    void onSelect(GraphSnapshot snapshot, GraphNode node);

    void onUnselect();

}