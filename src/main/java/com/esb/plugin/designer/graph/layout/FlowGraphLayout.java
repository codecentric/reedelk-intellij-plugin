package com.esb.plugin.designer.graph.layout;

import com.esb.plugin.designer.Tile;
import com.esb.plugin.designer.graph.DirectedGraph;
import com.esb.plugin.designer.graph.Node;

import java.util.Collections;
import java.util.List;

public class FlowGraphLayout {

    private final int X_LEFT_PADDING = 20;

    private final List<List<Node>> layers;
    private final DirectedGraph<Node> graph;

    public FlowGraphLayout(DirectedGraph<Node> graph) {
        this.graph = graph;
        FlowGraphLayers layers = new FlowGraphLayers(graph);
        this.layers = layers.compute();
    }

    public void compute() {
        _calculate(0, Collections.singletonList(graph.root()));
    }

    private void _calculate(int top, List<Node> nodes) {
        if (nodes.size() == 1) {
            Node node = nodes.get(0);
            List<Node> predecessors = graph.predecessors(node);

            // Root
            if (predecessors.isEmpty()) {
                centerInSubtree(top, node);

                // Single node with more than one predecessor
            } else {
                int tmpX = X_LEFT_PADDING + findLayer(node) * Tile.WIDTH;
                int min = predecessors.stream().mapToInt(Node::y).min().getAsInt();
                int max = predecessors.stream().mapToInt(Node::y).max().getAsInt();
                int tmpY = Math.floorDiv(max + min, 2);
                node.setPosition(tmpX, tmpY);

                _calculate(top, graph.successors(node));
            }

            // Layer with multiple nodes.
            // Center them all in their respective subtrees.
        } else if (nodes.size() > 1) {
            for (Node node : nodes) {
                top += centerInSubtree(top, node);
            }
        }
    }

    private int centerInSubtree(int top, Node node) {
        int maxSubtreeHeight = maxSubtreeNodes(node) * Tile.HEIGHT;
        int tmpX = X_LEFT_PADDING + findLayer(node) * Tile.WIDTH;
        int tmpY = top + Math.floorDiv(maxSubtreeHeight, 2);
        node.setPosition(tmpX, tmpY);

        _calculate(top, graph.successors(node));

        return maxSubtreeHeight;
    }

    private int maxSubtreeNodes(Node node) {
        List<Node> successors = graph.successors(node);
        return successors.isEmpty() ? 1 :
                successors.stream().mapToInt(this::maxSubtreeNodes).sum();
    }

    private int findLayer(Node current) {
        for (int i = 0; i < layers.size(); i++) {
            if (layers.get(i).contains(current)) {
                return i;
            }
        }
        throw new RuntimeException("Could not find layer for node " + current);
    }
}
