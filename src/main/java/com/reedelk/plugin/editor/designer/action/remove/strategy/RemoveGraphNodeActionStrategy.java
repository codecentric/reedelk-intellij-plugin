package com.reedelk.plugin.editor.designer.action.remove.strategy;

import com.reedelk.plugin.commons.GetSuccessorIndex;
import com.reedelk.plugin.commons.IsScopedGraphNode;
import com.reedelk.plugin.editor.designer.action.ActionStrategy;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.graph.node.ScopedGraphNode;
import com.reedelk.plugin.graph.utils.FindScope;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

import static com.reedelk.runtime.api.commons.Preconditions.checkState;

public class RemoveGraphNodeActionStrategy implements ActionStrategy {

    private final FlowGraph graph;
    private final PlaceholderProvider placeholderProvider;

    public RemoveGraphNodeActionStrategy(@NotNull FlowGraph graph, @NotNull PlaceholderProvider placeholderProvider) {
        this.graph = graph;
        this.placeholderProvider = placeholderProvider;
    }

    @Override
    public void execute(GraphNode toRemove) {
        List<GraphNode> predecessors = graph.predecessors(toRemove);
        List<GraphNode> successors = graph.successors(toRemove);

        GraphNode successor = successors.isEmpty() ? null : successors.get(0);

        if (predecessors.isEmpty()) {
            // It is the first node of the graph we don't have to connect any predecessor
            // with the current node successors. If the node to remove is  a scoped node
            // then it must be a node with empty scope. Otherwise the RemoveScopedGraphNodeActionStrategy
            // means that it has not been called beforehand.
            checkState(successors.size() <= 1, "Expected at most one successor");
            graph.remove(toRemove);

        } else {
            // This is a node with at least one predecessor. We must connect predecessors
            // with the node to remove successors.
            for (GraphNode predecessor : predecessors) {
                if (IsScopedGraphNode.of(predecessor)) {
                    removeSuccessorOfScopedNode(toRemove, (ScopedGraphNode) predecessor, successor);
                } else {
                    removeSuccessorOfNodeStrategy(toRemove, predecessor, successor);
                }
            }

            // Remove the node from any scope it might belong to
            Optional<ScopedGraphNode> selectedScope = FindScope.of(graph, toRemove);
            selectedScope.ifPresent(scopedNode -> scopedNode.removeFromScope(toRemove));
        }
    }

    private void removeSuccessorOfNodeStrategy(GraphNode toRemove, GraphNode predecessor, GraphNode successor) {
        graph.remove(predecessor, toRemove);

        if (successor != null) graph.remove(toRemove, successor);

        graph.remove(toRemove);

        FindScope.of(graph, toRemove).ifPresent(scopedGraphNode ->
                scopedGraphNode.removeFromScope(scopedGraphNode));

        if (successor != null) graph.add(predecessor, successor);
    }

    private void removeSuccessorOfScopedNode(GraphNode toRemove, ScopedGraphNode predecessor, GraphNode successor) {
        int index = GetSuccessorIndex.ofScopedNode(graph, predecessor, toRemove);

        graph.remove(predecessor, toRemove);
        FindScope.of(graph, toRemove)
                .ifPresent(scopedGraphNode -> scopedGraphNode.removeFromScope(toRemove));

        if (successor != null) {
            graph.remove(toRemove, successor);

            if (predecessor.scopeContains(successor)) {
                graph.add(predecessor, successor, index);

            } else if (predecessor.getScope().isEmpty()) {
                // We need to connect the scoped node with the next
                // one only if it is empty.
                graph.add(predecessor, successor);
            }
        }

        graph.remove(toRemove);

        predecessor.onSuccessorRemoved(graph, toRemove, index, placeholderProvider);
    }
}
