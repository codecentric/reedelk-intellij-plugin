package com.esb.plugin.graph.action;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.action.strategy.Strategy;
import com.esb.plugin.graph.action.strategy.SubFlowStrategyBuilder;
import com.esb.plugin.graph.node.GraphNode;

import java.awt.*;
import java.awt.image.ImageObserver;

public class SubFlowActionNodeAdd extends ActionNodeAdd {

    public SubFlowActionNodeAdd(Point dropPoint, GraphNode node, Graphics2D graphics, ImageObserver observer) {
        super(dropPoint, node, graphics, observer);
    }

    @Override
    public void execute(FlowGraph graph) {
        Strategy strategy = SubFlowStrategyBuilder.create()
                .observer(observer)
                .dropPoint(dropPoint)
                .graphics(graphics)
                .graph(graph)
                .build();

        if (strategy.applicableOn(node)) {
            strategy.execute(node);
        }
    }
}
