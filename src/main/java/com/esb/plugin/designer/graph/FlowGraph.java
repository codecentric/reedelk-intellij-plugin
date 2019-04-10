package com.esb.plugin.designer.graph;

import com.esb.plugin.designer.graph.drawable.Drawable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public interface FlowGraph {

    void root(@NotNull Drawable n1);

    void add(@NotNull Drawable n1);

    void add(@Nullable Drawable n1, @NotNull Drawable n2);

    void add(@NotNull Drawable n1, @NotNull Drawable n2, int index);

    void remove(Drawable n1);

    void remove(Drawable n1, Drawable n2);

    List<Drawable> successors(@NotNull Drawable n1);

    List<Drawable> predecessors(@NotNull Drawable n1);

    int nodesCount();

    Collection<Drawable> nodes();

    boolean isEmpty();

    void breadthFirstTraversal(@NotNull Consumer<Drawable> consumer);

    Drawable root();

    FlowGraph copy();

    void removeEdgesStartingFrom(Drawable drawable);
}
