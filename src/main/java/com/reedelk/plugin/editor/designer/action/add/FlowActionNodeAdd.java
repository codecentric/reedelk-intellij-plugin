package com.reedelk.plugin.editor.designer.action.add;


import com.reedelk.plugin.editor.designer.action.Action;
import com.reedelk.plugin.editor.designer.action.Strategy;
import com.reedelk.plugin.editor.designer.action.add.strategy.FlowStrategyBuilder;
import com.reedelk.plugin.editor.designer.action.remove.strategy.PlaceholderProvider;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.node.GraphNode;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.image.ImageObserver;

/**
 * Adds to the graph a new node representing the Component Name to the given location.
 * This class find the best position where to place the node in the Graph given the drop point location.
 */
public class FlowActionNodeAdd implements Action {

    protected final PlaceholderProvider placeholderProvider;
    protected final ImageObserver observer;
    protected final Graphics2D graphics;
    protected final Point dropPoint;
    protected final GraphNode node;

    public FlowActionNodeAdd(@NotNull Point dropPoint,
                             @NotNull GraphNode node,
                             @NotNull Graphics2D graphics,
                             @NotNull ImageObserver observer,
                             @NotNull PlaceholderProvider placeholderProvider) {
        this.placeholderProvider = placeholderProvider;
        this.dropPoint = dropPoint;
        this.observer = observer;
        this.graphics = graphics;
        this.node = node;
    }

    @Override
    public void execute(FlowGraph graph) {
        Strategy strategy = FlowStrategyBuilder.create()
                .placeholderProvider(placeholderProvider)
                .dropPoint(dropPoint)
                .observer(observer)
                .graphics(graphics)
                .graph(graph)
                .build();

        if (strategy.applicableOn(node)) {
            strategy.execute(node);
        }
    }
}
