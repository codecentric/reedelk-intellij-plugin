package com.esb.plugin.graph.connector;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.FlowSubGraph;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.ScopedGraphNode;
import com.esb.plugin.graph.utils.ListLastNodeOfScope;

import java.util.Collection;

import static com.google.common.base.Preconditions.checkState;

public class ScopedNodeConnector implements Connector {

    private final FlowGraph graph;
    private final FlowSubGraph scopeSubGraph;

    public ScopedNodeConnector(final FlowGraph graph, final FlowSubGraph scopeSubGraph) {
        this.graph = graph;
        this.scopeSubGraph = scopeSubGraph;
    }

    @Override
    public void addSuccessor(GraphNode successor) {
        addScopeGraphIfNeeded();
        Collection<GraphNode> drawables = ListLastNodeOfScope.from(graph, (ScopedGraphNode) scopeSubGraph.root());
        drawables.forEach(drawable -> graph.add(drawable, successor));
    }

    @Override
    public void addPredecessor(GraphNode predecessor) {
        addScopeGraphIfNeeded();
        graph.add(predecessor, scopeSubGraph.root());
    }

    @Override
    public void add() {
        addScopeGraphIfNeeded();
    }

    @Override
    public void addPredecessor(ScopedGraphNode predecessor, int index) {
        addScopeGraphIfNeeded();
        graph.add(predecessor, scopeSubGraph.root(), index);
    }

    @Override
    public void addToScope(ScopedGraphNode scope) {
        checkState(graph.nodes().contains(scopeSubGraph.root()));
        scope.addToScope(scopeSubGraph.root());
    }

    @Override
    public void root() {
        throw new UnsupportedOperationException("Scoped node can not be set as root node");
    }

    private void addScopeGraphIfNeeded() {
        GraphNode rootOfScope = scopeSubGraph.root();
        boolean isAlreadyAdded = graph.nodes().contains(rootOfScope);
        if (!isAlreadyAdded) {
            graph.add(rootOfScope);
            addSubGraph(rootOfScope);
        }
    }

    private void addSubGraph(GraphNode parent) {
        scopeSubGraph.successors(parent).forEach(successor -> {
            graph.add(parent, successor);
            addSubGraph(successor);
        });
    }
}
