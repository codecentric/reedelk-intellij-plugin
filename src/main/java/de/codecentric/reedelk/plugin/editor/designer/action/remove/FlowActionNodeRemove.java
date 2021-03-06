package de.codecentric.reedelk.plugin.editor.designer.action.remove;

import de.codecentric.reedelk.plugin.editor.designer.action.Action;
import de.codecentric.reedelk.plugin.editor.designer.action.ActionStrategy;
import de.codecentric.reedelk.plugin.editor.designer.action.remove.strategy.FlowRemoveRootActionStrategy;
import de.codecentric.reedelk.plugin.editor.designer.action.remove.strategy.PlaceholderProvider;
import de.codecentric.reedelk.plugin.editor.designer.action.remove.strategy.RemoveGraphNodeActionStrategy;
import de.codecentric.reedelk.plugin.editor.designer.action.remove.strategy.RemoveScopedGraphNodeActionStrategy;
import de.codecentric.reedelk.plugin.graph.FlowGraph;
import de.codecentric.reedelk.plugin.graph.node.GraphNode;
import de.codecentric.reedelk.plugin.commons.IsScopedGraphNode;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static de.codecentric.reedelk.runtime.api.commons.Preconditions.checkState;

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

        ActionStrategy strategy;

        if (predecessors.isEmpty()) {
            // If the predecessor of the node to remove is empty,
            // we are removing root node.
            strategy = new FlowRemoveRootActionStrategy(graph, placeholderProvider);

        } else if (IsScopedGraphNode.of(toRemove)) {
            // Handle ScopedGraphNode
            strategy = new RemoveScopedGraphNodeActionStrategy(graph, placeholderProvider);

        } else {
            // This is a node with at most one successor, otherwise it
            // would be a scoped graph node.
            checkState(successors.size() <= 1, "Expected at most one successor");
            strategy = new RemoveGraphNodeActionStrategy(graph, placeholderProvider);
        }

        strategy.execute(toRemove);
    }

}
