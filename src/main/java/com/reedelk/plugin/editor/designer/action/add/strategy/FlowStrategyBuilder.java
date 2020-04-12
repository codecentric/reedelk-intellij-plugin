package com.reedelk.plugin.editor.designer.action.add.strategy;

import com.reedelk.plugin.commons.Half;
import com.reedelk.plugin.commons.LiesBetweenTopAndBottom;
import com.reedelk.plugin.editor.designer.action.ActionStrategy;
import com.reedelk.plugin.graph.FlowGraph;
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
    public ActionStrategy build() {

        if (graph.isEmpty()) {
            return new FlowAddRootAction(graph);

        } else if (isReplacingRoot(graph, dropPoint, graphics)) {
            return new FlowReplaceRootAction(graph, placeholderProvider);

        } else if (isOverlappingAnyPlaceHolder(graph, dropPoint)) {
            GraphNode overlappingPlaceholder = getOverlappingPlaceholder(graph, dropPoint);
            return new ReplaceNodeAction(graph, overlappingPlaceholder, placeholderProvider);

        } else {
            return FindClosestPrecedingNode.of(graph, dropPoint, graphics)
                    .map(this::strategyFromClosestPrecedingNode)
                    .orElseGet(NoOpAction::new);
        }
    }

    /**
     * We are replacing root if the drop point is before the current root OR
     * if the drop point is inside the current root node.
     */
    private static boolean isReplacingRoot(FlowGraph graph, Point dropPoint, Graphics2D graphics) {
        checkState(!graph.isEmpty(), "Expected a not empty graph");
        GraphNode root = graph.root();
        int halfWidth = Half.of(root.width(graphics));
        return dropPoint.x <= root.x() + halfWidth &&
                LiesBetweenTopAndBottom.of(root, dropPoint.y, graphics);
    }
}
