package com.esb.plugin.designer.graph;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

public class FlowGraphImpl implements FlowGraph {

    private DirectedGraph<GraphNode> graph;

    public FlowGraphImpl() {
    }

    private FlowGraphImpl(DirectedGraph<GraphNode> graph) {
        this.graph = graph;
    }

    @Override
    public void add(@NotNull GraphNode n1) {
        graph.addNode(n1);
    }

    @Override
    public void root(@NotNull GraphNode root) {
        if (graph == null) {
            graph = new DirectedGraph<>(root);
        } else {
            graph.root(root);
        }
    }

    @Override
    public void add(@Nullable GraphNode n1, @NotNull GraphNode n2) {
        if (n1 == null) {
            checkState(graph == null, "Root was not null");
            graph = new DirectedGraph<>(n2);
        } else {
            graph.putEdge(n1, n2);
        }
    }

    @Override
    public void add(@NotNull GraphNode n1, @NotNull GraphNode n2, int index) {
        graph.putEdge(n1, n2, index);
    }

    @Override
    public void remove(GraphNode n1) {
        graph.removeNode(n1);
    }

    @Override
    public void remove(GraphNode n1, GraphNode n2) {
        checkArgument(n1 != null, "n1");
        checkArgument(n2 != null, "n2");
        graph.removeEdge(n1, n2);
    }

    @Override
    public List<GraphNode> successors(@NotNull GraphNode n1) {
        return graph.successors(n1);
    }

    @Override
    public List<GraphNode> predecessors(@NotNull GraphNode n1) {
        return graph.predecessors(n1);
    }

    @Override
    public int nodesCount() {
        return graph.nodes().size();
    }

    @Override
    public Collection<GraphNode> nodes() {
        return graph != null ?
                graph.nodes() :
                Collections.emptyList();
    }

    @Override
    public boolean isEmpty() {
        return graph == null || graph.nodes().isEmpty();
    }

    @Override
    public void breadthFirstTraversal(@NotNull Consumer<GraphNode> consumer) {
        graph.breadthFirstTraversal(graph.root(), consumer);
    }

    @Override
    public GraphNode root() {
        return graph.root();
    }

    @Override
    public FlowGraph copy() {
        if (graph == null) {
            return new FlowGraphImpl();
        } else {
            return new FlowGraphImpl(graph.copy());
        }
    }

    @Override
    public void removeEdgesStartingFrom(GraphNode drawable) {
        graph.removeEdgesStartingFrom(drawable);
    }

}
