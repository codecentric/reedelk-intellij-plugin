package com.esb.plugin.designer.graph.layout;

import com.esb.plugin.designer.Tile;
import com.esb.plugin.designer.graph.DirectedGraph;
import com.esb.plugin.designer.graph.drawable.Drawable;
import com.esb.plugin.designer.graph.drawable.ScopedDrawable;

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

    public static int computeSubTreeHeight(DirectedGraph<Drawable> graph, Drawable root) {
        int max = graph.successors(root)
                .stream()
                .map(item -> FlowGraphLayout.computeSubTreeHeight(graph, item))
                .mapToInt(value -> value)
                .sum();
        int scopedDrawablePadding = 0;
        if (root instanceof ScopedDrawable) {
            scopedDrawablePadding = 5 + 5;
        }
        return max > root.height() ?
                max + scopedDrawablePadding :
                root.height() + scopedDrawablePadding;
    }

    private void _calculate(int top, List<Drawable> drawables) {
        if (drawables.size() == 1) {
            Drawable drawable = drawables.get(0);
            List<Drawable> predecessors = graph.predecessors(drawable);

            // Root
            if (predecessors.isEmpty()) {
                centerInSubtree(top, drawable);

                // Single node with one or more predecessor/s
            } else {
                int tmpX = X_LEFT_PADDING + findLayer(drawable) * Tile.WIDTH;
                int min = predecessors.stream().mapToInt(Drawable::y).min().getAsInt();
                int max = predecessors.stream().mapToInt(Drawable::y).max().getAsInt();
                int tmpY = Math.floorDiv(max + min, 2);

                drawable.setPosition(tmpX, tmpY);

                if (drawable instanceof ScopedDrawable) {
                    top += 5;
                }
                _calculate(top, graph.successors(drawable));
            }

            // Layer with multiple nodes.
            // Center them all in their respective subtrees.
            // Successors can be > 1 only when predecessor is ScopedDrawable
        } else if (drawables.size() > 1) {
            for (Drawable drawable : drawables) {

                if (drawable instanceof ScopedDrawable) {
                    top += 5;
                }

                int tmpX = X_LEFT_PADDING + findLayer(drawable) * Tile.WIDTH;

                int maxSubtreeHeight = computeSubTreeHeight(graph, drawable);
                // Need to subtract the current padding since we are adding it here.

                if (drawable instanceof ScopedDrawable) {
                    maxSubtreeHeight -= 5 + 5;
                }

                int tmpY = top + Math.floorDiv(maxSubtreeHeight, 2);
                drawable.setPosition(tmpX, tmpY);

                _calculate(top, graph.successors(drawable));

                if (drawable instanceof ScopedDrawable) {
                    top += 5;
                }

                top += maxSubtreeHeight;

            }
        }
    }

    private int findLayer(Drawable current) {
        for (int i = 0; i < layers.size(); i++) {
            if (layers.get(i).contains(current)) {
                return i;
            }
        }
        throw new RuntimeException("Could not find layer for node " + current);
    }

    private int centerInSubtree(int top, Drawable drawable) {
        int maxSubtreeHeight = computeSubTreeHeight(graph, drawable);
        int tmpX = X_LEFT_PADDING + findLayer(drawable) * Tile.WIDTH;
        int tmpY = top + Math.floorDiv(maxSubtreeHeight, 2);
        drawable.setPosition(tmpX, tmpY);

        _calculate(top, graph.successors(drawable));

        return maxSubtreeHeight;
    }

}
