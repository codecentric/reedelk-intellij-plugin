package com.esb.plugin.graph;

import com.esb.plugin.graph.node.GraphNode;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public class FlowSubGraph extends FlowGraphImpl {

    public FlowSubGraph() {
        super(FlowSubGraph.class.getName());
    }

    @Override
    public String id() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(@NotNull GraphNode n1) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void remove(GraphNode n1) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void remove(GraphNode n1, GraphNode n2) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isEmpty() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int nodesCount() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(@NotNull GraphNode n1, @NotNull GraphNode n2, int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<GraphNode> nodes() {
        throw new UnsupportedOperationException();
    }

    @Override
    public FlowGraph copy() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeEdgesStartingFrom(GraphNode node) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<GraphNode> predecessors(@NotNull GraphNode n1) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void breadthFirstTraversal(@NotNull Consumer<GraphNode> consumer) {
        throw new UnsupportedOperationException();
    }
}
