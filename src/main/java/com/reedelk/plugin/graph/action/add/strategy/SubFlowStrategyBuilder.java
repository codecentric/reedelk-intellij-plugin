package com.reedelk.plugin.graph.action.add.strategy;

import com.reedelk.plugin.commons.Half;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.action.Strategy;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.graph.utils.FindClosestPrecedingNode;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

import static com.google.common.base.Preconditions.checkState;

public class SubFlowStrategyBuilder extends StrategyBuilder {

    private SubFlowStrategyBuilder() {
    }

    public static SubFlowStrategyBuilder create() {
        return new SubFlowStrategyBuilder();
    }

    @NotNull
    @Override
    public Strategy build() {

        if (graph.isEmpty()) {
            return new SubFlowAddRootStrategy(graph);

        } else if (isBeforeRoot(graph, dropPoint, graphics)) {
            return new SubFlowAddNewRoot(graph);

        } else if (isOverlappingAnyPlaceHolder(graph, dropPoint)) {
            GraphNode overlappingPlaceholder = getOverlappingPlaceholder(graph, dropPoint);
            return new ReplaceNodeStrategy(graph, overlappingPlaceholder, placeholderProvider);

        } else {
            return FindClosestPrecedingNode.of(graph, dropPoint, graphics)
                    .map(this::getStrategyForClosestPrecedingNode)
                    .orElseGet(NoOpStrategy::new);
        }
    }

    private boolean isBeforeRoot(FlowGraph graph, Point dropPoint, Graphics2D graphics) {
        checkState(!graph.isEmpty(), "Expected a not empty graph");
        GraphNode root = graph.root();
        return dropPoint.x <= root.x() &&
                dropPoint.y > root.y() - Half.of(root.height(graphics)) &&
                dropPoint.y < root.y() + Half.of(root.height(graphics));
    }
}
