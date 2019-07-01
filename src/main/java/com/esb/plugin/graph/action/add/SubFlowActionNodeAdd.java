package com.esb.plugin.graph.action.add;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.action.Strategy;
import com.esb.plugin.graph.action.add.strategy.SubFlowStrategyBuilder;
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
