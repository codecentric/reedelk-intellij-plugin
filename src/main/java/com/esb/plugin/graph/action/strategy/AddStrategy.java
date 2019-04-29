package com.esb.plugin.graph.action.strategy;

import com.esb.plugin.graph.GraphNode;

public interface AddStrategy {

    void execute(GraphNode closestPrecedingDrawable);

}
