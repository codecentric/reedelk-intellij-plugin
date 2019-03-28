package com.esb.plugin.designer.graph;

import com.esb.plugin.designer.graph.drawable.Drawable;
import com.esb.plugin.designer.graph.layout.FlowGraphLayout;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import static com.google.common.base.Preconditions.checkState;

public class FlowGraph {

    private DirectedGraph<Drawable> graph;

    public void add(@Nullable Drawable n1, @NotNull Drawable n2) {
        if (n1 == null) {
            checkState(graph == null, "Root was not null");
            graph = new DirectedGraph<>(n2);
            graph.addNode(n2);
        } else {
            graph.putEdge(n1, n2);
        }
    }

    public List<Drawable> successors(@NotNull Drawable n1) {
        return graph.successors(n1);
    }

    public List<Drawable> predecessors(@NotNull Drawable target) {
        return graph.predecessors(target);
    }

    public int nodesCount() {
        return graph.nodes().size();
    }

    public Collection<Drawable> nodes() {
        return graph.nodes();
    }

    public void breadthFirstTraversal(@NotNull Drawable drawable, @NotNull Consumer<Drawable> consumer) {
        graph.breadthFirstTraversal(drawable, consumer);
    }

    public Drawable root() {
        return graph.root();
    }

    public void computePositions() {
        FlowGraphLayout positions = new FlowGraphLayout(graph);
        positions.compute();
    }
}
