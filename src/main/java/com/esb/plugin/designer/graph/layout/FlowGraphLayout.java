package com.esb.plugin.designer.graph.layout;

import com.esb.plugin.designer.Tile;
import com.esb.plugin.designer.graph.DirectedGraph;
import com.esb.plugin.designer.graph.Drawable;

import java.util.Collections;
import java.util.List;

public class FlowGraphLayout {

    private final int X_LEFT_PADDING = Math.floorDiv(Tile.WIDTH, 2);

    private final List<List<Drawable>> layers;
    private final DirectedGraph<Drawable> graph;

    public FlowGraphLayout(DirectedGraph<Drawable> graph) {
        this.graph = graph;
        FlowGraphLayers layers = new FlowGraphLayers(graph);
        this.layers = layers.compute();
    }

    public void compute() {
        _calculate(0, Collections.singletonList(graph.root()));
    }

    private void _calculate(int top, List<Drawable> drawables) {
        if (drawables.size() == 1) {
            Drawable drawable = drawables.get(0);
            List<Drawable> predecessors = graph.predecessors(drawable);

            // Root
            if (predecessors.isEmpty()) {
                centerInSubtree(top, drawable);

                // Single node with more than one predecessor
            } else {
                int tmpX = X_LEFT_PADDING + findLayer(drawable) * Tile.WIDTH;
                int min = predecessors.stream().mapToInt(Drawable::y).min().getAsInt();
                int max = predecessors.stream().mapToInt(Drawable::y).max().getAsInt();
                int tmpY = Math.floorDiv(max + min, 2);
                drawable.setPosition(tmpX, tmpY);

                _calculate(top, graph.successors(drawable));
            }

            // Layer with multiple nodes.
            // Center them all in their respective subtrees.
        } else if (drawables.size() > 1) {
            for (Drawable drawable : drawables) {
                top += centerInSubtree(top, drawable);
            }
        }
    }

    private int centerInSubtree(int top, Drawable drawable) {
        int maxSubtreeHeight = maxSubtreeNodes(drawable) * Tile.HEIGHT;
        int tmpX = X_LEFT_PADDING + findLayer(drawable) * Tile.WIDTH;
        int tmpY = top + Math.floorDiv(maxSubtreeHeight, 2);
        drawable.setPosition(tmpX, tmpY);

        _calculate(top, graph.successors(drawable));

        return maxSubtreeHeight;
    }

    private int maxSubtreeNodes(Drawable drawable) {
        List<Drawable> successors = graph.successors(drawable);
        return successors.isEmpty() ? 1 :
                successors.stream().mapToInt(this::maxSubtreeNodes).sum();
    }

    private int findLayer(Drawable current) {
        for (int i = 0; i < layers.size(); i++) {
            if (layers.get(i).contains(current)) {
                return i;
            }
        }
        throw new RuntimeException("Could not find layer for node " + current);
    }
}
