package com.reedelk.plugin.graph.action.remove;

import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.action.Action;
import com.reedelk.plugin.graph.action.Strategy;
import com.reedelk.plugin.graph.action.remove.strategy.PlaceholderProvider;
import com.reedelk.plugin.graph.action.remove.strategy.RemoveGraphNodeStrategy;
import com.reedelk.plugin.graph.action.remove.strategy.RemoveScopedGraphNodeStrategy;
import com.reedelk.plugin.graph.action.remove.strategy.SubflowRemoveRootStrategy;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.graph.node.ScopedGraphNode;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.reedelk.runtime.commons.Preconditions.checkState;

/**
 * Removes a single node from a Subflow. In a subflow when the root
 * node is removed, the first successor of the root takes new place
 * as root node.
 */
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
            // This is a node with at most one successor, otherwise it
            // would be a scoped graph node.
            checkState(successors.size() <= 1, "Expected at most one successor");
            strategy = new RemoveGraphNodeStrategy(graph);
        }

        strategy.execute(toRemove);
    }
}
