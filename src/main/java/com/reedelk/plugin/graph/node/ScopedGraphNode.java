package com.reedelk.plugin.graph.node;

import com.reedelk.plugin.graph.FlowGraph;

import java.awt.*;
import java.util.Collection;

public interface ScopedGraphNode extends GraphNode {

    int VERTICAL_PADDING = 5;
    int HORIZONTAL_PADDING = 5;

    void addToScope(GraphNode node);

    void removeFromScope(GraphNode node);

    Collection<GraphNode> getScope();

    boolean scopeContains(GraphNode node);

    ScopeBoundaries getScopeBoundaries(FlowGraph graph, Graphics2D graphics);

    int verticalDividerXOffset();

}
