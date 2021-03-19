package de.codecentric.reedelk.plugin.graph;

import de.codecentric.reedelk.plugin.graph.node.GraphNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public interface FlowGraph {

    String id();

    String title();

    void setTitle(String title);

    String description();

    void setDescription(String description);

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

    List<GraphNode> endNodes();

    void removeEdgesStartingFrom(GraphNode node);

    void breadthFirstTraversal(@NotNull Consumer<GraphNode> consumer);

    default boolean isError() {
        return false;
    }

    default Throwable getError() {
        // An error is set if and only if isError returns true;
        throw new UnsupportedOperationException();
    }
}
