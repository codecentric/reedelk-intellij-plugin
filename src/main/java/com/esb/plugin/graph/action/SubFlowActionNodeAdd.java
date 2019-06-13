package com.esb.plugin.graph.action;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.action.strategy.Strategy;
import com.esb.plugin.graph.action.strategy.StrategyBuilder;
import com.esb.plugin.graph.node.GraphNode;

import java.awt.*;
import java.awt.image.ImageObserver;

public class SubFlowActionNodeAdd extends ActionNodeAdd {

    public SubFlowActionNodeAdd(Point dropPoint, GraphNode node, Graphics2D graphics, ImageObserver observer) {
        super(dropPoint, node, graphics, observer);
    }

    @Override
    public void execute(FlowGraph graph) {
        Strategy strategy = StrategyBuilder.create()
                .imageObserver(observer)
                .dropPoint(dropPoint)
                .graphics(graphics)
                .graph(graph)
                .subflow()
                .build();
        strategy.execute(node);
    }
}
