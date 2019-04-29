package com.esb.plugin.designer.graph.connector;

import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.GraphNode;
import com.esb.plugin.designer.graph.drawable.ScopedDrawable;
import com.esb.plugin.designer.graph.scope.ListLastNodeOfScope;

import java.util.Collection;

import static com.google.common.base.Preconditions.checkState;

public class ScopeDrawableConnector implements Connector {

    private final FlowGraph graph;
    private final FlowGraph scopeGraph;

    public ScopeDrawableConnector(final FlowGraph graph, final FlowGraph scopeGraph) {
        this.graph = graph;
        this.scopeGraph = scopeGraph;
    }

    @Override
    public void addSuccessor(GraphNode successor) {
        addScopeGraphIfNeeded();
        Collection<GraphNode> drawables = ListLastNodeOfScope.from(graph, (ScopedDrawable) scopeGraph.root());
        drawables.forEach(drawable -> graph.add(drawable, successor));
    }

    @Override
    public void addPredecessor(GraphNode predecessor) {
        addScopeGraphIfNeeded();
        graph.add(predecessor, scopeGraph.root());
    }

    @Override
    public void add() {
        addScopeGraphIfNeeded();
    }

    @Override
    public void addPredecessor(ScopedDrawable predecessor, int index) {
        addScopeGraphIfNeeded();
        graph.add(predecessor, scopeGraph.root(), index);
    }

    @Override
    public void addToScope(ScopedDrawable scope) {
        checkState(graph.nodes().contains(scopeGraph.root()));
        scope.addToScope(scopeGraph.root());
    }

    @Override
    public void root() {
        throw new UnsupportedOperationException("Scope Drawable can not be root");
    }

    private void addScopeGraphIfNeeded() {
        GraphNode rootOfScope = scopeGraph.root();
        boolean isAlreadyAdded = graph.nodes().contains(rootOfScope);
        if (!isAlreadyAdded) {
            graph.add(rootOfScope);
            addSubGraph(rootOfScope);
        }
    }

    private void addSubGraph(GraphNode parent) {
        scopeGraph.successors(parent).forEach(successor -> {
            graph.add(parent, successor);
            addSubGraph(successor);
        });
    }
}
