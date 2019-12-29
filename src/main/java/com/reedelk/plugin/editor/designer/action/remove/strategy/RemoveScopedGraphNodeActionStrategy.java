package com.reedelk.plugin.editor.designer.action.remove.strategy;

import com.reedelk.plugin.commons.IsScopedGraphNode;
import com.reedelk.plugin.editor.designer.action.ActionStrategy;
import com.reedelk.plugin.editor.designer.action.remove.FlowActionNodeRemove;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.graph.node.ScopedGraphNode;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;

import static com.reedelk.runtime.api.commons.Preconditions.checkState;

public class RemoveScopedGraphNodeActionStrategy implements ActionStrategy {

    private final PlaceholderProvider absentPlaceholderProvider = new AbsentPlaceholderProvider();
    private final PlaceholderProvider placeholderProvider;
    private final FlowGraph graph;

    public RemoveScopedGraphNodeActionStrategy(@NotNull FlowGraph graph, PlaceholderProvider placeholderProvider) {
        this.graph = graph;
        this.placeholderProvider = placeholderProvider;
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
        // We must use in this case the original placeholder provider so that the notified
        // predecessors of the node being removed are provided with the correct original placeholder
        // provider. This for instance is needed when we remove a ScopedGraphNode which is the first
        // successor of another ScopedGraphNode. When we remove the nested ScopedGraphNode and we
        // notify the parent ScopedGraphNode we must use the original placeholder provider since
        // the parent is not being removed.
        ActionStrategy strategy = new RemoveGraphNodeActionStrategy(graph, placeholderProvider);
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
            if (IsScopedGraphNode.of(nodeToRemove)) {
                removeNestedScopesNodes((ScopedGraphNode) nodeToRemove);

                // Remove the current scoped node
                ActionStrategy strategy = new RemoveGraphNodeActionStrategy(graph, absentPlaceholderProvider);
                strategy.execute(nodeToRemove);
            } else {
                FlowActionNodeRemove action = new FlowActionNodeRemove(nodeToRemove, absentPlaceholderProvider);
                action.execute(graph);
            }
        }
    }
}
