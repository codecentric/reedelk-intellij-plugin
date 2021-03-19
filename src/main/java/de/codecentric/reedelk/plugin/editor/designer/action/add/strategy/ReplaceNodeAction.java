package de.codecentric.reedelk.plugin.editor.designer.action.add.strategy;

import de.codecentric.reedelk.plugin.graph.FlowGraph;
import de.codecentric.reedelk.plugin.graph.node.GraphNode;
import de.codecentric.reedelk.plugin.graph.node.ScopedGraphNode;
import de.codecentric.reedelk.plugin.graph.utils.FindScope;
import de.codecentric.reedelk.plugin.commons.GetSuccessorIndex;
import de.codecentric.reedelk.plugin.commons.IsPredecessorScopedNode;
import de.codecentric.reedelk.plugin.editor.designer.action.ActionStrategy;
import de.codecentric.reedelk.plugin.editor.designer.action.remove.strategy.PlaceholderProvider;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static de.codecentric.reedelk.runtime.api.commons.Preconditions.checkState;

/**
 * Strategy which replaces a node with the given replacement node.
 */
public class ReplaceNodeAction implements ActionStrategy {

    private final PlaceholderProvider placeholderProvider;
    private final GraphNode toBeReplaced;
    private final FlowGraph graph;

    ReplaceNodeAction(@NotNull FlowGraph graph,
                      @NotNull GraphNode toBeReplaced,
                      @NotNull PlaceholderProvider placeholderProvider) {
        this.placeholderProvider = placeholderProvider;
        this.toBeReplaced = toBeReplaced;
        this.graph = graph;
    }

    @Override
    public void execute(GraphNode replacement) {

        // Connect predecessors
        List<GraphNode> predecessorsOfPlaceHolder = graph.predecessors(toBeReplaced);
        if (predecessorsOfPlaceHolder.isEmpty()) {
            // If there are no predecessors, then the placeholder must be the root node.
            checkState(graph.root() == toBeReplaced,
                    "Expected Placeholder to be root node but it was not");
            graph.root(replacement);
        }

        if (IsPredecessorScopedNode.of(predecessorsOfPlaceHolder)) {
            // If the predecessors is only one and it is a scoped node we must
            // replace the 'replacement' at the index of the 'toBeReplaced' node.
            ScopedGraphNode predecessorScopedGraphNode = (ScopedGraphNode) predecessorsOfPlaceHolder.get(0);
            int index = GetSuccessorIndex.ofScopedNode(graph, predecessorScopedGraphNode, toBeReplaced);
            graph.add(predecessorScopedGraphNode, replacement, index);

            List<GraphNode> successorsOfPlaceHolder = graph.successors(toBeReplaced);
            successorsOfPlaceHolder.forEach(successor -> graph.add(replacement, successor));

            // If the placeholder belongs to a scope, we must
            // remove it from the scope and add the replacement to the scope.
            FindScope.of(graph, toBeReplaced).ifPresent(scopeNode -> {
                scopeNode.addToScope(replacement);
                scopeNode.removeFromScope(toBeReplaced);
            });

            // Remove the placeholder node from the graph (including inbound/outbound edges)
            graph.remove(toBeReplaced);

            replacement.onAdded(graph, placeholderProvider);

            predecessorScopedGraphNode.onSuccessorAdded(graph, replacement, index);

        } else {
            predecessorsOfPlaceHolder.forEach(predecessor -> graph.add(predecessor, replacement));

            List<GraphNode> successorsOfPlaceHolder = graph.successors(toBeReplaced);
            successorsOfPlaceHolder.forEach(successor -> graph.add(replacement, successor));

            // If the placeholder belongs to a scope, we must
            // remove it from the scope and add the replacement to the scope.
            FindScope.of(graph, toBeReplaced).ifPresent(scopeNode -> {
                scopeNode.addToScope(replacement);
                scopeNode.removeFromScope(toBeReplaced);
            });

            // Remove the placeholder node from the graph (including inbound/outbound edges)
            graph.remove(toBeReplaced);

            replacement.onAdded(graph, placeholderProvider);

        }
    }
}
