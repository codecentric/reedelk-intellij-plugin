package com.reedelk.plugin.editor.designer.action.add.strategy;

import com.reedelk.plugin.editor.designer.action.ActionStrategy;
import com.reedelk.plugin.editor.designer.action.remove.strategy.PlaceholderProvider;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.node.GraphNode;
import org.jetbrains.annotations.NotNull;

/**
 * In a Subflow we don't replace the root node. We just add a node before.
 */
public class SubFlowAddNewRootAction implements ActionStrategy {

    private final FlowGraph graph;
    private final PlaceholderProvider placeholderProvider;

    SubFlowAddNewRootAction(@NotNull FlowGraph graph,
                            @NotNull PlaceholderProvider placeholderProvider) {
        this.graph = graph;
        this.placeholderProvider = placeholderProvider;
    }

    @Override
    public void execute(GraphNode node) {

        GraphNode currentRoot = graph.root();

        graph.root(node);

        graph.add(node, currentRoot);

        node.onAdded(graph, placeholderProvider);
    }
}
