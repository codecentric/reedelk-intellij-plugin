package com.esb.plugin.graph.action.remove.strategy;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.action.Strategy;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.utils.FindScope;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RemoveSuccessorOfNodeStrategy implements Strategy {

    private final FlowGraph graph;
    private final GraphNode successor;
    private final GraphNode predecessor;

    public RemoveSuccessorOfNodeStrategy(@NotNull FlowGraph graph, @NotNull GraphNode predecessor, @Nullable GraphNode successor) {
        this.graph = graph;
        this.successor = successor;
        this.predecessor = predecessor;
    }

    @Override
    public void execute(GraphNode toRemove) {
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
}
