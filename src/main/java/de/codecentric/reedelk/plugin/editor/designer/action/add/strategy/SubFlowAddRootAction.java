package de.codecentric.reedelk.plugin.editor.designer.action.add.strategy;

import de.codecentric.reedelk.plugin.graph.FlowGraph;
import de.codecentric.reedelk.plugin.graph.node.GraphNode;
import de.codecentric.reedelk.plugin.editor.designer.action.ActionStrategy;
import de.codecentric.reedelk.plugin.editor.designer.action.remove.strategy.PlaceholderProvider;
import org.jetbrains.annotations.NotNull;

/**
 * A Subflow can only start with components which don't have
 * INBOUND class type.
 */
public class SubFlowAddRootAction implements ActionStrategy {

    private final FlowGraph graph;
    private final PlaceholderProvider placeholderProvider;

    SubFlowAddRootAction(@NotNull FlowGraph graph,
                         @NotNull PlaceholderProvider placeholderProvider) {
        this.graph = graph;
        this.placeholderProvider = placeholderProvider;
    }

    @Override
    public void execute(GraphNode node) {

        graph.root(node);

        node.onAdded(graph, placeholderProvider);
    }
}
