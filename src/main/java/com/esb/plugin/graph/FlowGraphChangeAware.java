package com.esb.plugin.graph;

import com.esb.plugin.graph.node.GraphNode;
import com.intellij.openapi.module.Module;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public class FlowGraphChangeAware implements FlowGraph {

    private final FlowGraph wrapped;
    private boolean changed = false;

    public FlowGraphChangeAware(@NotNull FlowGraph wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public String id() {
        return wrapped.id();
    }

    @Override
    public void root(@NotNull GraphNode n1) {
        wrapped.root(n1);
        changed = true;
    }

    @Override
    public void add(@NotNull GraphNode n1) {
        wrapped.add(n1);
        changed = true;
    }

    @Override
    public void add(@Nullable GraphNode n1, @NotNull GraphNode n2) {
        wrapped.add(n1, n2);
        changed = true;
    }

    @Override
    public void add(@NotNull GraphNode n1, @NotNull GraphNode n2, int index) {
        wrapped.add(n1, n2, index);
        changed = true;
    }

    @Override
    public void remove(GraphNode n1) {
        wrapped.remove(n1);
        changed = true;
    }

    @Override
    public void remove(@NotNull GraphNode n1, @NotNull GraphNode n2) {
        wrapped.remove(n1, n2);
        changed = true;
    }

    @Override
    public void removeEdgesStartingFrom(GraphNode node) {
        wrapped.removeEdgesStartingFrom(node);
        changed = true;
    }

    @Override
    public List<GraphNode> successors(@NotNull GraphNode n1) {
        return wrapped.successors(n1);
    }

    @Override
    public List<GraphNode> predecessors(@NotNull GraphNode n1) {
        return wrapped.predecessors(n1);
    }

    @Override
    public int nodesCount() {
        return wrapped.nodesCount();
    }

    @Override
    public Collection<GraphNode> nodes() {
        return wrapped.nodes();
    }

    @Override
    public boolean isEmpty() {
        return wrapped.isEmpty();
    }

    @Override
    public void breadthFirstTraversal(@NotNull Consumer<GraphNode> consumer) {
        wrapped.breadthFirstTraversal(consumer);
    }

    @Override
    public GraphNode root() {
        return wrapped.root();
    }

    @Override
    public FlowGraph copy() {
        return wrapped.copy();
    }

    public boolean isChanged() {
        return changed;
    }

    public void commit(Module module) {
        wrapped.breadthFirstTraversal(node ->
                node.commit(FlowGraphChangeAware.this, module));
    }
}
