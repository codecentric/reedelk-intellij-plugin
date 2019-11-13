package com.reedelk.plugin.editor.designer.action.add;

import com.reedelk.plugin.editor.designer.action.Action;
import com.reedelk.plugin.editor.designer.action.Strategy;
import com.reedelk.plugin.editor.designer.action.add.strategy.SubFlowStrategyBuilder;
import com.reedelk.plugin.editor.designer.action.remove.strategy.PlaceholderProvider;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.node.GraphNode;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.image.ImageObserver;

public class SubFlowActionNodeAdd implements Action {

    private final PlaceholderProvider placeholderProvider;
    protected final ImageObserver observer;
    protected final Graphics2D graphics;
    protected final Point dropPoint;
    protected final GraphNode node;

    public SubFlowActionNodeAdd(@NotNull Point dropPoint,
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
        Strategy strategy = SubFlowStrategyBuilder.create()
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
