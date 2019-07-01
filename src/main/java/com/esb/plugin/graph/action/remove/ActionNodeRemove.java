package com.esb.plugin.graph.action.remove;

import com.esb.plugin.component.type.placeholder.PlaceholderNode;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.action.Action;
import com.esb.plugin.graph.action.NoOpStrategy;
import com.esb.plugin.graph.action.Strategy;
import com.esb.plugin.graph.action.remove.strategy.RemoveRootStrategy;
import com.esb.plugin.graph.action.remove.strategy.RemoveSuccessorOfNodeStrategy;
import com.esb.plugin.graph.action.remove.strategy.RemoveSuccessorOfScopedNodeStrategy;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.ScopedGraphNode;

import java.util.Collection;
import java.util.List;

import static com.esb.internal.commons.Preconditions.checkState;

/**
 * Removes a single node from the graph.
 */
public class ActionNodeRemove implements Action {

    private final GraphNode toRemove;
    private final PlaceholderProvider placeholderProvider;

    public ActionNodeRemove(PlaceholderProvider placeholderProvider, final GraphNode toRemove) {
        this.placeholderProvider = placeholderProvider;
        this.toRemove = toRemove;
    }

    @Override
    public void execute(FlowGraph graph) {
        List<GraphNode> predecessors = graph.predecessors(toRemove);
        List<GraphNode> successors = graph.successors(toRemove);

        checkPreconditions(successors);

        // If the predecessor of the node to remove is empty,
        // then it means that we are removing root node.
        Strategy strategy = new NoOpStrategy();
        if (predecessors.isEmpty()) {
            strategy = new RemoveRootStrategy(graph, placeholderProvider);
        } else {
            GraphNode successor = successors.isEmpty() ? null : successors.get(0);
            for (GraphNode predecessor : predecessors) {
                if (predecessor instanceof ScopedGraphNode) {
                    strategy = new RemoveSuccessorOfScopedNodeStrategy(graph, predecessor, successor);
                } else {
                    strategy = new RemoveSuccessorOfNodeStrategy(graph, predecessor, successor);
                }
            }
        }
        strategy.execute(toRemove);
    }

    private void checkPreconditions(List<GraphNode> successors) {
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
    }

    public interface PlaceholderProvider {
        PlaceholderNode get();
    }
}