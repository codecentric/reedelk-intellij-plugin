package com.esb.plugin.graph.action.remove;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.action.Action;
import com.esb.plugin.graph.action.Strategy;
import com.esb.plugin.graph.action.remove.strategy.PlaceholderProvider;
import com.esb.plugin.graph.action.remove.strategy.RemoveGraphNodeStrategy;
import com.esb.plugin.graph.action.remove.strategy.RemoveScopedGraphNodeStrategy;
import com.esb.plugin.graph.action.remove.strategy.SubflowRemoveRootStrategy;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.ScopedGraphNode;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.esb.internal.commons.Preconditions.checkState;

public class SubFlowActionNodeRemove implements Action {

    private final PlaceholderProvider placeholderProvider;
    private final GraphNode toRemove;

    public SubFlowActionNodeRemove(@NotNull GraphNode toRemove,
                                   @NotNull PlaceholderProvider placeholderProvider) {
        this.placeholderProvider = placeholderProvider;
        this.toRemove = toRemove;
    }

    @Override
    public void execute(FlowGraph graph) {
        List<GraphNode> predecessors = graph.predecessors(toRemove);
        List<GraphNode> successors = graph.successors(toRemove);

        Strategy strategy;

        if (predecessors.isEmpty()) {
            // If the predecessor of the node to remove is empty,
            // we are removing root node.
            strategy = new SubflowRemoveRootStrategy(graph, placeholderProvider);

        } else if (toRemove instanceof ScopedGraphNode) {
            // Handle ScopedGraphNode
            strategy = new RemoveScopedGraphNodeStrategy(graph, placeholderProvider);

        } else {
            // This is a node with potentially many successors (if it follows a ScopedGraphNode)
            // and at most one successor - because if it would not have at most one  successor
            // it would be a scoped graph node -
            checkState(successors.size() <= 1, "Expected at most one successor");
            strategy = new RemoveGraphNodeStrategy(graph);
        }

        strategy.execute(toRemove);
    }
}
