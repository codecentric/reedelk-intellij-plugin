package com.reedelk.plugin.editor.designer.action.remove.strategy;

import com.reedelk.plugin.commons.Labels.Placeholder;
import com.reedelk.plugin.editor.designer.action.Strategy;
import com.reedelk.plugin.graph.FlowGraph;
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

            // If we remove the root, we need to replace it with the placeholder.
            placeholderProvider.get(Placeholder.DESCRIPTION_INBOUND).ifPresent(placeholder -> {
                // Set the new root.
                graph.root(placeholder);

                // Root node MUST have only one successor.
                graph.add(placeholder, successors.get(0));
            });
        }

        graph.remove(root);
    }
}
