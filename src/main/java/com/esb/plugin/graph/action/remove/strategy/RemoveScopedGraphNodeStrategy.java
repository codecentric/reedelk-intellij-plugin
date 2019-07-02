package com.esb.plugin.graph.action.remove.strategy;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.action.Strategy;
import com.esb.plugin.graph.action.remove.ActionNodeRemove;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.ScopedGraphNode;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;

import static com.esb.internal.commons.Preconditions.checkState;
import static com.esb.plugin.graph.action.remove.ActionNodeRemove.PlaceholderProvider;

public class RemoveScopedGraphNodeStrategy implements com.esb.plugin.graph.action.Strategy {

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

        // First remove all the nodes belonging to this scope and nested scope
        removeNestedScopesNodes(scopeToRemove);

        // We make sure that if the node to be removed
        // is a scoped node, then all its nodes in the scope
        // have already been removed as well.
        ScopedGraphNode scopedGraphNode = (ScopedGraphNode) toRemove;
        Collection<GraphNode> scope = scopedGraphNode.getScope();
        checkState(scope.isEmpty(),
                "Before removing a scoped node remove all the nodes belonging to its own (and nested) scope/s");

        Strategy strategy = new RemoveGraphNodeStrategy(graph);
        strategy.execute(scopedGraphNode);
    }

    private void removeNestedScopesNodes(ScopedGraphNode scopedGraphNode) {
        Collection<GraphNode> scope = scopedGraphNode.getScope();
        Collection<GraphNode> copyOfScope = new ArrayList<>(scope);
        copyOfScope.forEach(new RemoveScopeNodeConsumer());
    }

    private class RemoveScopeNodeConsumer implements Consumer<GraphNode> {
        @Override
        public void accept(GraphNode node) {
            if (node instanceof ScopedGraphNode) {
                removeNestedScopesNodes((ScopedGraphNode) node);

                // Remove the current scoped node
                Strategy strategy = new RemoveGraphNodeStrategy(graph);
                strategy.execute(node);
            } else {
                ActionNodeRemove action = new ActionNodeRemove(placeholderProvider, node);
                action.execute(graph);
            }
        }
    }
}
