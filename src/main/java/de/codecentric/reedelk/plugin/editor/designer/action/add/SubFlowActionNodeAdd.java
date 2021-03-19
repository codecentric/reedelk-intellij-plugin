package de.codecentric.reedelk.plugin.editor.designer.action.add;

import de.codecentric.reedelk.plugin.editor.designer.action.Action;
import de.codecentric.reedelk.plugin.editor.designer.action.ActionStrategy;
import de.codecentric.reedelk.plugin.editor.designer.action.add.strategy.SubFlowStrategyBuilder;
import de.codecentric.reedelk.plugin.graph.FlowGraph;
import de.codecentric.reedelk.plugin.graph.node.GraphNode;
import de.codecentric.reedelk.plugin.editor.designer.action.remove.strategy.PlaceholderProvider;
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
        ActionStrategy strategy = SubFlowStrategyBuilder.create()
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
