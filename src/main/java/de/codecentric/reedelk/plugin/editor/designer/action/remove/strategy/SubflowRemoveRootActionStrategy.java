package de.codecentric.reedelk.plugin.editor.designer.action.remove.strategy;

import de.codecentric.reedelk.plugin.editor.designer.action.ActionStrategy;
import de.codecentric.reedelk.plugin.graph.FlowGraph;
import de.codecentric.reedelk.plugin.graph.node.GraphNode;
import de.codecentric.reedelk.plugin.graph.node.ScopedGraphNode;
import de.codecentric.reedelk.plugin.graph.utils.FindFirstNodeOutsideScope;
import de.codecentric.reedelk.plugin.commons.IsScopedGraphNode;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static de.codecentric.reedelk.runtime.api.commons.Preconditions.checkState;

public class SubflowRemoveRootActionStrategy implements ActionStrategy {

    private final PlaceholderProvider placeholderProvider;
    private final FlowGraph graph;

    public SubflowRemoveRootActionStrategy(@NotNull FlowGraph graph, @NotNull PlaceholderProvider placeholderProvider) {
        this.graph = graph;
        this.placeholderProvider = placeholderProvider;
    }

    @Override
    public void execute(GraphNode root) {
        List<GraphNode> successors = graph.successors(root);

        if (!successors.isEmpty()) {
            if (IsScopedGraphNode.of(root)) {
                // The new root is he first node outside the scope
                FindFirstNodeOutsideScope.of(graph, (ScopedGraphNode) root)
                        .ifPresent(graph::root);
            } else {
                checkState(successors.size() == 1, "Expected exactly one successor");
                graph.root(successors.get(0));
            }
        }

        if (IsScopedGraphNode.of(root)) {
            RemoveScopedGraphNodeActionStrategy removeScopedGraphNodeStrategy =
                    new RemoveScopedGraphNodeActionStrategy(graph, placeholderProvider);
            removeScopedGraphNodeStrategy.execute(root);
        } else {
            graph.remove(root);
        }
    }
}
