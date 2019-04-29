package com.esb.plugin.designer.graph.action.strategy;

import com.esb.plugin.designer.graph.GraphNode;

public interface AddStrategy {

    void execute(GraphNode closestPrecedingDrawable);

}
