package com.esb.plugin.graph.action.strategy;

import com.esb.plugin.graph.node.GraphNode;

public interface AddStrategy {

    void execute(GraphNode closestPrecedingDrawable);

}
