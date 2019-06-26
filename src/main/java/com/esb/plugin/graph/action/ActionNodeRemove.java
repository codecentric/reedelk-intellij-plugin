package com.esb.plugin.graph.action;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.GraphNodeFactory;
import com.esb.plugin.graph.node.ScopedGraphNode;
import com.esb.plugin.graph.utils.FindScope;
import com.esb.system.component.Placeholder;
import com.intellij.openapi.module.Module;

import java.util.List;

import static com.esb.internal.commons.Preconditions.checkState;

// TODO: Test me
public class ActionNodeRemove {

    private final GraphNode toBeRemoved;
    private final FlowGraph graph;
    private final Module module;

    public ActionNodeRemove(Module module, final FlowGraph copy, final GraphNode toBeRemoved) {
        this.toBeRemoved = toBeRemoved;
        this.module = module;
        this.graph = copy;
    }

    public void remove() {
        List<GraphNode> predecessors = graph.predecessors(toBeRemoved);
        List<GraphNode> successors = graph.successors(toBeRemoved);

        // We might remove the node right outside the scope, which
        // might have more than one predecessor
        checkState(successors.size() <= 1, "Expected at most one successor");

        if (predecessors.isEmpty()) {
            // If we remove the root and it does not have successors,
            // we cannot remove the successors.
            if (!successors.isEmpty()) {
                // If we remove the root, we need to replace
                // it with the placeholder.
                GraphNode placeholder = GraphNodeFactory.get(module, Placeholder.class.getName());
                graph.root(placeholder);
                graph.add(placeholder, successors.get(0));
            }
            graph.remove(toBeRemoved);

        } else {

            GraphNode successor = successors.isEmpty() ? null : successors.get(0);

            for (GraphNode predecessor : predecessors) {
                if (predecessor instanceof ScopedGraphNode) {
                    ScopedGraphNode scopedPredecessor = (ScopedGraphNode) predecessor;
                    int index = getDroppedIndex(scopedPredecessor, toBeRemoved);
                    graph.remove(predecessor, toBeRemoved);

                    FindScope.of(graph, toBeRemoved)
                            .ifPresent(scopedGraphNode -> scopedGraphNode.removeFromScope(toBeRemoved));

                    if (successor != null) {
                        graph.remove(toBeRemoved, successor);
                        if (scopedPredecessor.scopeContains(successor)) {
                            graph.add(predecessor, successor, index);

                            // We need to connect the scoped node with the next one only
                            // if it is empty.
                        } else if (scopedPredecessor.getScope().isEmpty()) {
                            graph.add(predecessor, successor);
                        }
                    }

                    graph.remove(toBeRemoved);

                } else {
                    graph.remove(predecessor, toBeRemoved);
                    if (successor != null) graph.remove(toBeRemoved, successor);
                    graph.remove(toBeRemoved);
                    FindScope.of(graph, toBeRemoved)
                            .ifPresent(scopedGraphNode -> scopedGraphNode.removeFromScope(scopedGraphNode));
                    if (successor != null) graph.add(predecessor, successor);
                }
            }
        }
    }

    private int getDroppedIndex(ScopedGraphNode scopedPredecessor, GraphNode dropped) {
        List<GraphNode> successors = graph.successors(scopedPredecessor);
        for (int i = 0; i < successors.size(); i++) {
            if (successors.get(i) == dropped) return i;
        }
        // This is the case where we need to find a toBeRemoved index
        // for a scoped predecessor without successors in the scope.
        // In this case, the index is just 0 since it is the first to be connected.
        return 0;
    }
}
