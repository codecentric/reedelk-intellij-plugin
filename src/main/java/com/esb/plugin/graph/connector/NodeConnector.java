package com.esb.plugin.graph.connector;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.ScopedDrawable;

public class NodeConnector implements Connector {

    private final GraphNode node;
    private final FlowGraph graph;

    public NodeConnector(final FlowGraph graph, final GraphNode node) {
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
