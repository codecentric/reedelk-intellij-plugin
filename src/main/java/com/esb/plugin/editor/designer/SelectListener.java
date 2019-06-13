package com.esb.plugin.editor.designer;

import com.esb.plugin.graph.FlowSnapshot;
import com.esb.plugin.graph.node.GraphNode;

public interface SelectListener {

    void onSelect(FlowSnapshot snapshot, GraphNode node);

    void onUnselect();

}
