package com.esb.plugin.designer.graph.connector;

import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.GraphNode;
import com.esb.plugin.designer.graph.drawable.ScopedDrawable;

public class DrawableConnector implements Connector {

    private final GraphNode node;
    private final FlowGraph graph;

    public DrawableConnector(final FlowGraph graph, final GraphNode node) {
        this.graph = graph;
        this.node = node;
    }

    @Override
    public void addSuccessor(GraphNode successor) {
        graph.add(node, successor);
    }

    @Override
    public void addPredecessor(GraphNode predecessor) {
        graph.add(predecessor, node);
    }

    @Override
    public void add() {
        graph.add(node);
    }

    @Override
    public void root() {
        graph.root(node);
    }

    @Override
    public void addToScope(ScopedDrawable scope) {
        scope.addToScope(node);
    }

    @Override
    public void addPredecessor(ScopedDrawable predecessor, int index) {
        graph.add(predecessor, node, index);
    }
}
