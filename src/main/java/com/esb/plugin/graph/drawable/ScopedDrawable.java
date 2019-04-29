package com.esb.plugin.graph.drawable;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.GraphNode;

import java.awt.*;
import java.util.Collection;

public interface ScopedDrawable extends GraphNode {

    int VERTICAL_PADDING = 5;
    int HORIZONTAL_PADDING = 5;

    void addToScope(GraphNode drawable);

    void removeFromScope(GraphNode drawable);

    Collection<GraphNode> getScope();

    boolean scopeContains(GraphNode drawable);

    ScopeBoundaries getScopeBoundaries(FlowGraph graph, Graphics2D graphics);

}
