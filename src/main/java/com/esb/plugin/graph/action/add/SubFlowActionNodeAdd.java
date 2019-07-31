package com.esb.plugin.graph.action.add;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.action.Action;
import com.esb.plugin.graph.action.Strategy;
import com.esb.plugin.graph.action.add.strategy.SubFlowStrategyBuilder;
import com.esb.plugin.graph.node.GraphNode;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.image.ImageObserver;

public class SubFlowActionNodeAdd implements Action {

    protected final ImageObserver observer;
    protected final Graphics2D graphics;
    protected final Point dropPoint;
    protected final GraphNode node;

    public SubFlowActionNodeAdd(@NotNull Point dropPoint,
                                @NotNull GraphNode node,
                                @NotNull Graphics2D graphics,
                                @NotNull ImageObserver observer) {
        this.dropPoint = dropPoint;
        this.observer = observer;
        this.graphics = graphics;
        this.node = node;
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
