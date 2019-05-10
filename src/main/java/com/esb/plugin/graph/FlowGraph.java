package com.esb.plugin.graph;

import com.esb.plugin.graph.node.GraphNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public interface FlowGraph {

    String id();

    boolean isEmpty();

    FlowGraph copy();

    GraphNode root();

    void root(@NotNull GraphNode n1);

    int nodesCount();

    Collection<GraphNode> nodes();

    void add(@NotNull GraphNode n1);

    void add(@Nullable GraphNode n1, @NotNull GraphNode n2);

    void add(@NotNull GraphNode n1, @NotNull GraphNode n2, int index);

    void remove(GraphNode n1);

    void remove(GraphNode n1, GraphNode n2);

    List<GraphNode> successors(@NotNull GraphNode n1);

    List<GraphNode> predecessors(@NotNull GraphNode n1);

    void removeEdgesStartingFrom(GraphNode node);

    void breadthFirstTraversal(@NotNull Consumer<GraphNode> consumer);
}
