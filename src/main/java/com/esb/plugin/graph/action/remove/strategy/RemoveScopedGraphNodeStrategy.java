package com.esb.plugin.graph.action.remove.strategy;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.action.Strategy;
import com.esb.plugin.graph.action.remove.FlowActionNodeRemove;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.ScopedGraphNode;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;

import static com.esb.internal.commons.Preconditions.checkState;

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
        public void accept(GraphNode nodeToRemove) {
            if (nodeToRemove instanceof ScopedGraphNode) {
                removeNestedScopesNodes((ScopedGraphNode) nodeToRemove);

                // Remove the current scoped node
                Strategy strategy = new RemoveGraphNodeStrategy(graph);
                strategy.execute(nodeToRemove);
            } else {
                FlowActionNodeRemove action = new FlowActionNodeRemove(nodeToRemove, placeholderProvider);
                action.execute(graph);
            }
        }
    }
}
