package com.esb.plugin.graph.connector;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.ScopedNode;
import com.esb.plugin.graph.scope.ListLastNodeOfScope;

import java.util.Collection;

import static com.google.common.base.Preconditions.checkState;

public class ScopedNodeConnector implements Connector {

    private final FlowGraph graph;
    private final FlowGraph scopeInitialSubtree;

    public ScopedNodeConnector(final FlowGraph graph, final FlowGraph scopeInitialSubtree) {
        this.graph = graph;
        this.scopeInitialSubtree = scopeInitialSubtree;
    }

    @Override
    public void addSuccessor(GraphNode successor) {
        addScopeGraphIfNeeded();
        Collection<GraphNode> drawables = ListLastNodeOfScope.from(graph, (ScopedNode) scopeInitialSubtree.root());
        drawables.forEach(drawable -> graph.add(drawable, successor));
    }

    @Override
    public void addPredecessor(GraphNode predecessor) {
        addScopeGraphIfNeeded();
        graph.add(predecessor, scopeInitialSubtree.root());
    }

    @Override
    public void add() {
        addScopeGraphIfNeeded();
    }

    @Override
    public void addPredecessor(ScopedNode predecessor, int index) {
        addScopeGraphIfNeeded();
        graph.add(predecessor, scopeInitialSubtree.root(), index);
    }

    @Override
    public void addToScope(ScopedNode scope) {
        checkState(graph.nodes().contains(scopeInitialSubtree.root()));
        scope.addToScope(scopeInitialSubtree.root());
    }

    @Override
    public void root() {
        throw new UnsupportedOperationException("Scope Drawable can not be root");
    }

    private void addScopeGraphIfNeeded() {
        GraphNode rootOfScope = scopeInitialSubtree.root();
        boolean isAlreadyAdded = graph.nodes().contains(rootOfScope);
        if (!isAlreadyAdded) {
            graph.add(rootOfScope);
            addSubGraph(rootOfScope);
        }
    }

    private void addSubGraph(GraphNode parent) {
        scopeInitialSubtree.successors(parent).forEach(successor -> {
            graph.add(parent, successor);
            addSubGraph(successor);
        });
    }
}
