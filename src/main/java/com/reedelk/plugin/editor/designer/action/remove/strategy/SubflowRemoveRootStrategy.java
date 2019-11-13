package com.reedelk.plugin.editor.designer.action.remove.strategy;

import com.reedelk.plugin.editor.designer.action.Strategy;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.graph.node.ScopedGraphNode;
import com.reedelk.plugin.graph.utils.FindFirstNodeOutsideScope;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.reedelk.runtime.commons.Preconditions.checkState;

public class SubflowRemoveRootStrategy implements Strategy {

    private final PlaceholderProvider placeholderProvider;
    private final FlowGraph graph;

    public SubflowRemoveRootStrategy(@NotNull FlowGraph graph, @NotNull PlaceholderProvider placeholderProvider) {
        this.graph = graph;
        this.placeholderProvider = placeholderProvider;
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
