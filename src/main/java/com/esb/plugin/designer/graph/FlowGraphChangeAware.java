package com.esb.plugin.designer.graph;

import com.esb.plugin.designer.graph.drawable.Drawable;
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
    public void root(@NotNull Drawable n1) {
        wrapped.root(n1);
        changed = true;
    }

    @Override
    public void add(@NotNull Drawable n1) {
        wrapped.add(n1);
        changed = true;
    }

    @Override
    public void add(@Nullable Drawable n1, @NotNull Drawable n2) {
        wrapped.add(n1, n2);
        changed = true;
    }

    @Override
    public void add(@NotNull Drawable n1, @NotNull Drawable n2, int index) {
        wrapped.add(n1, n2, index);
        changed = true;
    }

    @Override
    public void remove(Drawable n1) {
        wrapped.remove(n1);
        changed = true;
    }

    @Override
    public void remove(Drawable n1, Drawable n2) {
        wrapped.remove(n1, n2);
        changed = true;
    }

    @Override
    public void removeEdgesStartingFrom(Drawable n1) {
        wrapped.removeEdgesStartingFrom(n1);
        changed = true;
    }

    @Override
    public List<Drawable> successors(@NotNull Drawable n1) {
        return wrapped.successors(n1);
    }

    @Override
    public List<Drawable> predecessors(@NotNull Drawable n1) {
        return wrapped.predecessors(n1);
    }

    @Override
    public int nodesCount() {
        return wrapped.nodesCount();
    }

    @Override
    public Collection<Drawable> nodes() {
        return wrapped.nodes();
    }

    @Override
    public boolean isEmpty() {
        return wrapped.isEmpty();
    }

    @Override
    public void breadthFirstTraversal(@NotNull Consumer<Drawable> consumer) {
        wrapped.breadthFirstTraversal(consumer);
    }

    @Override
    public Drawable root() {
        return wrapped.root();
    }

    @Override
    public FlowGraph copy() {
        return wrapped.copy();
    }

    public boolean isChanged() {
        return changed;
    }
}
