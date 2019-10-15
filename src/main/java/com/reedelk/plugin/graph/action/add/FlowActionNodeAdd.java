package com.reedelk.plugin.graph.action.add;


import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.action.Action;
import com.reedelk.plugin.graph.action.Strategy;
import com.reedelk.plugin.graph.action.add.strategy.FlowStrategyBuilder;
import com.reedelk.plugin.graph.node.GraphNode;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.image.ImageObserver;

/**
 * Adds to the graph a new node representing the Component Name to the given location.
 * This class find the best position where to place the node in the Graph given the drop point location.
 */
public class FlowActionNodeAdd implements Action {

    protected final ImageObserver observer;
    protected final Graphics2D graphics;
    protected final Point dropPoint;
    protected final GraphNode node;

    public FlowActionNodeAdd(@NotNull Point dropPoint,
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
        Strategy strategy = FlowStrategyBuilder.create()
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