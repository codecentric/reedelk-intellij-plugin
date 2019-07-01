package com.esb.plugin.graph.action.add.strategy;

import com.esb.plugin.commons.Half;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.utils.FindClosestPrecedingNode;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Optional;

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
            return new FlowReplaceRootStrategy(graph);

        } else if (isOverlappingAnyPlaceHolder(graph, dropPoint)) {
            GraphNode overlappingPlaceholder = getOverlappingPlaceholder(graph, dropPoint);
            return new ReplaceNodeStrategy(graph, overlappingPlaceholder);

        } else {
            Optional<GraphNode> closestPrecedingNode = FindClosestPrecedingNode.of(graph, dropPoint, graphics);
            return closestPrecedingNode
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
