package de.codecentric.reedelk.plugin.editor.designer.action.add.strategy;

import de.codecentric.reedelk.plugin.graph.FlowGraph;
import de.codecentric.reedelk.plugin.graph.node.GraphNode;
import de.codecentric.reedelk.plugin.graph.utils.FindClosestPrecedingNode;
import de.codecentric.reedelk.plugin.commons.Half;
import de.codecentric.reedelk.plugin.editor.designer.action.ActionStrategy;
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
    public ActionStrategy build() {

        if (graph.isEmpty()) {
            return new SubFlowAddRootAction(graph, placeholderProvider);

        } else if (isBeforeRoot(graph, dropPoint, graphics)) {
            return new SubFlowAddNewRootAction(graph, placeholderProvider);

        } else if (isOverlappingAnyPlaceHolder(graph, dropPoint)) {
            GraphNode overlappingPlaceholder = getOverlappingPlaceholder(graph, dropPoint);
            return new ReplaceNodeAction(graph, overlappingPlaceholder, placeholderProvider);

        } else {
            return FindClosestPrecedingNode.of(graph, dropPoint, graphics)
                    .map(this::strategyFromClosestPrecedingNode)
                    .orElseGet(NoOpAction::new);
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
