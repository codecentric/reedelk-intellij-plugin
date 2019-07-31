package com.esb.plugin.graph.action.remove.strategy;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.action.Strategy;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.ScopedGraphNode;
import com.esb.plugin.graph.utils.FindFirstNodeOutsideScope;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.esb.internal.commons.Preconditions.checkState;

public class SubflowRemoveRootStrategy implements Strategy {

    private final PlaceholderProvider placeholderProvider;
    private final FlowGraph graph;

    public SubflowRemoveRootStrategy(@NotNull FlowGraph graph,
                                     @NotNull PlaceholderProvider placeholderProvider) {
        this.placeholderProvider = placeholderProvider;
        this.graph = graph;
    }

    @Override
    public void execute(GraphNode root) {
        List<GraphNode> successors = graph.successors(root);

        if (!successors.isEmpty()) {
            if (root instanceof ScopedGraphNode) {
                // The new root is he first node outside the scope
                FindFirstNodeOutsideScope.of(graph, (ScopedGraphNode) root)
                        .ifPresent(graph::root);
            } else {
                checkState(successors.size() == 1,
                        "Expected exactly one successor");
                graph.root(successors.get(0));
            }
        }

        if (root instanceof ScopedGraphNode) {
            RemoveScopedGraphNodeStrategy removeScopedGraphNodeStrategy =
                    new RemoveScopedGraphNodeStrategy(graph, placeholderProvider);
            removeScopedGraphNodeStrategy.execute(root);
        } else {
            graph.remove(root);
        }
    }
}
