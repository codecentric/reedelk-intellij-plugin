package com.esb.plugin.designer.graph;

import com.esb.plugin.designer.graph.layout.FlowGraphLayout;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

import static com.google.common.base.Preconditions.checkState;

public class FlowGraph {

    private DirectedGraph<Node> graph;

    public void add(@Nullable Node n1, @NotNull Node n2) {
        if (n1 == null) {
            checkState(graph == null, "Root was not null");
            graph = new DirectedGraph<>(n2);
            graph.addNode(n2);
        } else {
            graph.putEdge(n1, n2);
        }
    }

    public List<Node> successors(@NotNull Node n1) {
        return graph.successors(n1);
    }

    public Node root() {
        return graph.root();
    }

    public void computePositions() {
        FlowGraphLayout positions = new FlowGraphLayout(graph);
        positions.compute();
    }

    public void breadthFirstTraversal(Node node, Consumer<Node> consumer) {
        graph.breadthFirstTraversal(node, consumer);
    }

}
