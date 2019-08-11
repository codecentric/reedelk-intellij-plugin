package com.reedelk.plugin.graph.action.remove.strategy;

import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.action.Strategy;
import com.reedelk.plugin.graph.node.GraphNode;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class FlowRemoveRootStrategy implements Strategy {

    private final FlowGraph graph;
    private final PlaceholderProvider placeholderProvider;

    public FlowRemoveRootStrategy(@NotNull FlowGraph graph,
                                  @NotNull PlaceholderProvider placeholderProvider) {
        this.graph = graph;
        this.placeholderProvider = placeholderProvider;
    }

    @Override
    public void execute(GraphNode root) {
        List<GraphNode> successors = graph.successors(root);

        // If we remove the root and it has any successor,
        // we cannot remove the root node, and we add a placeholder node.
        if (!successors.isEmpty()) {

            // If we remove the root, we need to replace
            // it with the placeholder.
            GraphNode placeholder = placeholderProvider.get();

            graph.root(placeholder);

            graph.add(placeholder, successors.get(0));
        }

        graph.remove(root);
    }
}
