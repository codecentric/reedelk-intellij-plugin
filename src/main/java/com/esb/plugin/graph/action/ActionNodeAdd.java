package com.esb.plugin.graph.action;


import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.action.strategy.Strategy;
import com.esb.plugin.graph.action.strategy.StrategyBuilder;
import com.esb.plugin.graph.node.GraphNode;

import java.awt.*;
import java.awt.image.ImageObserver;

/**
 * Adds to the graph a new node representing the Component Name to the given location.
 * This class find the best position where to place the node in the Graph given the drop point location.
 */
public class ActionNodeAdd {

    private final ImageObserver observer;
    private final Graphics2D graphics;
    private final FlowGraph graph;
    private final Point dropPoint;
    private final GraphNode node;

    public ActionNodeAdd(final FlowGraph graph, final Point dropPoint, final GraphNode node, final Graphics2D graphics, final ImageObserver observer) {
        this.dropPoint = dropPoint;
        this.observer = observer;
        this.graphics = graphics;
        this.graph = graph;
        this.node = node;
    }

    public void execute() {
        Strategy strategy = StrategyBuilder.create()
                .imageObserver(observer)
                .dropPoint(dropPoint)
                .graphics(graphics)
                .graph(graph)
                .build();
        strategy.execute(node);
    }
}
