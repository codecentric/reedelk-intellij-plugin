package com.esb.plugin.graph.action.strategy;

import com.esb.plugin.graph.node.GraphNode;

public interface Strategy {

    void execute(GraphNode closestPrecedingDrawable);

}
