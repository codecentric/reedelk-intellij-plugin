package com.esb.plugin.graph.action.remove.strategy;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.action.Strategy;
import com.esb.plugin.graph.node.GraphNode;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.esb.internal.commons.Preconditions.checkState;

public class SubflowRemoveRootStrategy implements Strategy {

    private final FlowGraph graph;

    public SubflowRemoveRootStrategy(@NotNull FlowGraph graph) {
        this.graph = graph;
    }

    @Override
    public void execute(GraphNode root) {
        List<GraphNode> successors = graph.successors(root);
        checkState(successors.size() <= 1, "Expected at most one successor");

        if (!successors.isEmpty()) {
            graph.root(successors.get(0));
        }

        graph.remove(root);
    }
}
