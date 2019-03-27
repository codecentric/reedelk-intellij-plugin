package com.esb.plugin.graph;

import com.esb.plugin.graph.handler.Drawable;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import com.google.common.graph.Traverser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;

public class FlowGraph {

    private MutableGraph<Drawable> graph;
    private Drawable root;

    public FlowGraph() {
        this.graph = GraphBuilder.directed().build();
    }

    public void add(@Nullable Drawable n1, @NotNull Drawable n2) {
        if (n1 == null) {
            checkArgument(root == null, "Root was not null");
            root = n2;
            graph.addNode(n2);
        } else {
            graph.putEdge(n1, n2);
        }
    }

    public Set<Drawable> successors(@NotNull Drawable n1) {
        return graph.successors(n1);
    }

    public Drawable root() {
        return root;
    }

    public Iterable<Drawable> depthFirstPostOrder() {
        return Traverser
                .forGraph(graph)
                .depthFirstPostOrder(root);
    }

    public Iterable<Drawable> breadthFirst() {
        return Traverser
                .forGraph(graph)
                .breadthFirst(root);
    }
}
