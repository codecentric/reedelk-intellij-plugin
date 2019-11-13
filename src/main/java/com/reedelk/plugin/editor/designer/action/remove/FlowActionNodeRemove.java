package com.reedelk.plugin.editor.designer.action.remove;

import com.reedelk.plugin.commons.IsScopedGraphNode;
import com.reedelk.plugin.editor.designer.action.Action;
import com.reedelk.plugin.editor.designer.action.Strategy;
import com.reedelk.plugin.editor.designer.action.remove.strategy.FlowRemoveRootStrategy;
import com.reedelk.plugin.editor.designer.action.remove.strategy.PlaceholderProvider;
import com.reedelk.plugin.editor.designer.action.remove.strategy.RemoveGraphNodeStrategy;
import com.reedelk.plugin.editor.designer.action.remove.strategy.RemoveScopedGraphNodeStrategy;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.node.GraphNode;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.reedelk.runtime.commons.Preconditions.checkState;

/**
 * Removes a single node from a Flow. In a Flow if the root node is
 * removed, a placeholder is added, because the only type of node
 * allowed as root is 'inbound'.
 */
public class FlowActionNodeRemove implements Action {

    private final PlaceholderProvider placeholderProvider;
    private final GraphNode toRemove;

    public FlowActionNodeRemove(@NotNull GraphNode toRemove,
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
            strategy = new FlowRemoveRootStrategy(graph, placeholderProvider);

        } else if (IsScopedGraphNode.of(toRemove)) {
            // Handle ScopedGraphNode
            strategy = new RemoveScopedGraphNodeStrategy(graph, placeholderProvider);

        } else {
            // This is a node with at most one successor, otherwise it
            // would be a scoped graph node.
            checkState(successors.size() <= 1, "Expected at most one successor");
            strategy = new RemoveGraphNodeStrategy(graph, placeholderProvider);
        }

        strategy.execute(toRemove);
    }

}
