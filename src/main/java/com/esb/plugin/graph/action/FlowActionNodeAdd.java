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
public class FlowActionNodeAdd extends ActionNodeAdd {

    public FlowActionNodeAdd(Point dropPoint, GraphNode node, Graphics2D graphics, ImageObserver observer) {
        super(dropPoint, node, graphics, observer);
    }

    @Override
    public void execute(FlowGraph graph) {
        Strategy strategy = StrategyBuilder.create()
                .imageObserver(observer)
                .dropPoint(dropPoint)
                .graphics(graphics)
                .graph(graph)
                .build();
        strategy.execute(node);
    }
}
