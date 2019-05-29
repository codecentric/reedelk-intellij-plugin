package com.esb.plugin.graph.connector;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.ScopedGraphNode;

public class DefaultNodeConnector implements Connector {

    private final GraphNode node;
    private final FlowGraph graph;

    public DefaultNodeConnector(final FlowGraph graph, final GraphNode node) {
        this.graph = graph;
        this.node = node;
    }

    @Override
    public void addSuccessor(GraphNode successor) {
        if (node.isSuccessorAllowed(graph, successor)) {
            graph.add(node, successor);
            node.onSuccessorAdded(graph, successor);
        }
    }

    @Override
    public void addPredecessor(GraphNode predecessor) {
        if (predecessor.isSuccessorAllowed(graph, node)) {
            graph.add(predecessor, node);
            predecessor.onSuccessorAdded(graph, node);
        }
    }

    @Override
    public void addPredecessor(ScopedGraphNode predecessor, int index) {
        if (predecessor.isSuccessorAllowed(graph, node, index)) {
            graph.add(predecessor, node, index);
            predecessor.onSuccessorAdded(graph, node, index);
        }
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
    public void addToScope(ScopedGraphNode scope) {
        scope.addToScope(node);
    }
}
