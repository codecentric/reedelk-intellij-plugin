package com.esb.plugin.graph.action.remove;

import com.esb.plugin.component.type.placeholder.PlaceholderNode;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.action.Action;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.ScopedGraphNode;
import com.esb.plugin.graph.utils.FindScope;

import java.util.Collection;
import java.util.List;

import static com.esb.internal.commons.Preconditions.checkState;

/**
 * Removes a single node from the graph.
 */
public class ActionNodeRemove implements Action {

    private final GraphNode toRemove;
    private final FlowGraph graph;
    private final PlaceholderProvider placeholderProvider;

    @Override
    public void execute(FlowGraph graph) {

    }

    public interface PlaceholderProvider {
        PlaceholderNode get();
    }

    public ActionNodeRemove(PlaceholderProvider placeholderProvider, final FlowGraph copy, final GraphNode toRemove) {
        this.placeholderProvider = placeholderProvider;
        this.toRemove = toRemove;
        this.graph = copy;
    }

    public void remove() {
        List<GraphNode> predecessors = graph.predecessors(toRemove);
        List<GraphNode> successors = graph.successors(toRemove);

        if (toRemove instanceof ScopedGraphNode) {
            // We make sure that if the node to be removed
            // is a scoped node, then all its nodes in the scope
            // have already been removed as well.
            ScopedGraphNode scopedGraphNode = (ScopedGraphNode) toRemove;
            Collection<GraphNode> scope = scopedGraphNode.getScope();
            checkState(scope.isEmpty(),
                    "Before removing a scoped node remove all the nodes belonging to its own (and nested) scope/s");
        }

        checkState(successors.size() <= 1, "Expected at most one successor");

        // If the predecessor of the node to remove is empty,
        // then it means that we are removing root node.
        if (predecessors.isEmpty()) {
            removeRoot(successors);

        } else {
            GraphNode successor = successors.isEmpty() ? null : successors.get(0);
            for (GraphNode predecessor : predecessors) {
                if (predecessor instanceof ScopedGraphNode) {
                    removeSuccessorOfScopedGraphNode(successor, predecessor);
                } else {
                    removeSuccessorOfGraphNode(predecessor, successor);
                }
            }
        }
    }

    private void removeSuccessorOfGraphNode(GraphNode predecessor, GraphNode successor) {
        graph.remove(predecessor, toRemove);

        if (successor != null) {
            graph.remove(toRemove, successor);
        }

        graph.remove(toRemove);

        FindScope.of(graph, toRemove)
                .ifPresent(scopedGraphNode ->
                        scopedGraphNode.removeFromScope(scopedGraphNode));

        if (successor != null) {
            graph.add(predecessor, successor);
        }
    }

    private void removeSuccessorOfScopedGraphNode(GraphNode successor, GraphNode predecessor) {
        ScopedGraphNode scopedPredecessor = (ScopedGraphNode) predecessor;
        int index = getDroppedIndex(scopedPredecessor, toRemove);
        graph.remove(predecessor, toRemove);

        FindScope.of(graph, toRemove)
                .ifPresent(scopedGraphNode -> scopedGraphNode.removeFromScope(toRemove));

        if (successor != null) {
            graph.remove(toRemove, successor);
            if (scopedPredecessor.scopeContains(successor)) {
                graph.add(predecessor, successor, index);

                // We need to connect the scoped node with the next one only
                // if it is empty.
            } else if (scopedPredecessor.getScope().isEmpty()) {
                graph.add(predecessor, successor);
            }
        }

        graph.remove(toRemove);
    }

    private void removeRoot(List<GraphNode> successors) {
        // If we remove the root and it has any successor,
        // we cannot remove the root node, and we add a placeholder node.
        if (!successors.isEmpty()) {
            // If we remove the root, we need to replace
            // it with the placeholder.
            GraphNode placeholder = placeholderProvider.get();
            graph.root(placeholder);
            graph.add(placeholder, successors.get(0));
        }
        graph.remove(toRemove);
    }

    private int getDroppedIndex(ScopedGraphNode scopedPredecessor, GraphNode dropped) {
        List<GraphNode> successors = graph.successors(scopedPredecessor);
        for (int i = 0; i < successors.size(); i++) {
            if (successors.get(i) == dropped) return i;
        }
        // This is the case where we need to find a toRemove index
        // for a scoped predecessor without successors in the scope.
        // In this case, the index is just 0 since it is the first to be connected.
        return 0;
    }
}
