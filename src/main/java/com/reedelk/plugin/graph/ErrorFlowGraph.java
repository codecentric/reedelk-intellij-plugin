package com.reedelk.plugin.graph;

import com.reedelk.plugin.graph.node.GraphNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public class ErrorFlowGraph implements FlowGraph {

    private final Throwable error;

    public ErrorFlowGraph(Throwable error) {
        this.error = error;
    }

    @Override
    public String id() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String title() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setTitle(String title) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String description() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setDescription(String description) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isEmpty() {
        throw new UnsupportedOperationException();
    }

    @Override
    public FlowGraph copy() {
        throw new UnsupportedOperationException();
    }

    @Override
    public GraphNode root() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void root(@NotNull GraphNode n1) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int nodesCount() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<GraphNode> nodes() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(@NotNull GraphNode n1) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(@Nullable GraphNode n1, @NotNull GraphNode n2) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(@NotNull GraphNode n1, @NotNull GraphNode n2, int index) {
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
    public List<GraphNode> successors(@NotNull GraphNode n1) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<GraphNode> predecessors(@NotNull GraphNode n1) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<GraphNode> endNodes() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeEdgesStartingFrom(GraphNode node) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void breadthFirstTraversal(@NotNull Consumer<GraphNode> consumer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isError() {
        return true;
    }

    @Override
    public Throwable getError() {
        return error;
    }
}
