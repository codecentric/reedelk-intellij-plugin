package com.esb.plugin.graph.manager;

import com.esb.plugin.graph.FlowGraph;

/**
 * Topic to notify that the graph has been changed from the designer.
 */
public interface GraphChangeListener {

    void onGraphChanged(FlowGraph updatedGraph);

}
