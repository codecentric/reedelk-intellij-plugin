package com.reedelk.plugin.graph.action.remove.strategy;

import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.action.Strategy;
import com.reedelk.plugin.graph.action.remove.FlowActionNodeRemove;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.graph.node.ScopedGraphNode;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Consumer;

import static com.reedelk.runtime.commons.Preconditions.checkState;

public class RemoveScopedGraphNodeStrategy implements com.reedelk.plugin.graph.action.Strategy {

    private final PlaceholderProvider placeholderProvider;
    private final FlowGraph graph;

    public RemoveScopedGraphNodeStrategy(@NotNull FlowGraph graph,
                                         @NotNull PlaceholderProvider placeholderProvider) {
        this.placeholderProvider = placeholderProvider;
        this.graph = graph;
    }

    @Override
    public void execute(GraphNode toRemove) {
        ScopedGraphNode scopeToRemove = (ScopedGraphNode) toRemove;

        // First remove all the nodes belonging to this scope and nested scopes
        removeNestedScopesNodes(scopeToRemove);

        // We make sure that if the node to be removed
        // is a scoped node, then all its nodes in the scope
        // have already been removed as well.
        Collection<GraphNode> scope = scopeToRemove.getScope();
        checkState(scope.isEmpty(),
                "Before removing a scoped node remove all the nodes belonging to its own (and nested) scope/s");

        // Then we just remove it as a normal node.
        Strategy strategy = new RemoveGraphNodeStrategy(graph, placeholderProvider);
        strategy.execute(scopeToRemove);
    }

    private void removeNestedScopesNodes(ScopedGraphNode scopedGraphNode) {
        Collection<GraphNode> scope = scopedGraphNode.getScope();
        Collection<GraphNode> copyOfScope = new ArrayList<>(scope);
        copyOfScope.forEach(new RemoveScopeNodeConsumer());
    }

    private class RemoveScopeNodeConsumer implements Consumer<GraphNode> {
        @Override
        public void accept(GraphNode nodeToRemove) {
            if (nodeToRemove instanceof ScopedGraphNode) {
                removeNestedScopesNodes((ScopedGraphNode) nodeToRemove);

                // Remove the current scoped node
                Strategy strategy = new RemoveGraphNodeStrategy(graph, placeholderProvider);
                strategy.execute(nodeToRemove);
            } else {
                FlowActionNodeRemove action = new FlowActionNodeRemove(nodeToRemove, Optional::empty);
                action.execute(graph);
            }
        }
    }
}
