package com.reedelk.plugin.graph.action.add.strategy;

import com.reedelk.plugin.commons.Half;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.action.Strategy;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.graph.utils.FindClosestPrecedingNode;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

import static com.google.common.base.Preconditions.checkState;

public class FlowStrategyBuilder extends StrategyBuilder {

    private FlowStrategyBuilder() {
    }

    public static FlowStrategyBuilder create() {
        return new FlowStrategyBuilder();
    }

    @NotNull
    @Override
    public Strategy build() {

        if (graph.isEmpty()) {
            return new FlowAddRootStrategy(graph);

        } else if (isReplacingRoot(graph, dropPoint, graphics)) {
            return new FlowReplaceRootStrategy(graph, placeholderProvider);

        } else if (isOverlappingAnyPlaceHolder(graph, dropPoint)) {
            GraphNode overlappingPlaceholder = getOverlappingPlaceholder(graph, dropPoint);
            return new ReplaceNodeStrategy(graph, overlappingPlaceholder, placeholderProvider);

        } else {
            return FindClosestPrecedingNode.of(graph, dropPoint, graphics)
                    .map(this::getStrategyForClosestPrecedingNode)
                    .orElseGet(NoOpStrategy::new);
        }
    }

    /**
     * We are replacing root if the drop point is before the current root OR
     * if the drop point is inside the current root node.
     */
    private static boolean isReplacingRoot(FlowGraph graph, Point dropPoint, Graphics2D graphics) {
        checkState(!graph.isEmpty(), "Expected a not empty graph");
        GraphNode root = graph.root();
        return dropPoint.x <= root.x() + Half.of(root.width(graphics)) &&
                dropPoint.y > root.y() - Half.of(root.height(graphics)) &&
                dropPoint.y < root.y() + Half.of(root.height(graphics));

    }
}
