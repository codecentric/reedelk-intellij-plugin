package com.esb.plugin.designer.graph;

import com.esb.plugin.designer.Tile;
import com.esb.plugin.designer.graph.drawable.Drawable;
import com.esb.plugin.designer.graph.drawable.ScopedDrawable;

import java.awt.*;
import java.util.Collections;
import java.util.List;

public class FlowGraphLayout {

    private static final int X_LEFT_PADDING = Math.floorDiv(Tile.WIDTH, 2);


    public static void compute(FlowGraph graph, Graphics2D graphics) {
        FlowGraphLayers layers = new FlowGraphLayers(graph);
        List<List<Drawable>> layersList = layers.compute();
        _calculate(0, graph, graphics, Collections.singletonList(graph.root()), layersList);
    }

    public static int computeSubTreeHeight(FlowGraph graph, Drawable root, Graphics2D graphics) {
        int max = graph.successors(root)
                .stream()
                .map(item -> FlowGraphLayout.computeSubTreeHeight(graph, item, graphics))
                .mapToInt(value -> value)
                .sum();
        int scopedDrawablePadding = 0;
        if (root instanceof ScopedDrawable) {
            scopedDrawablePadding = 5 + 5;
        }
        return max > root.height(graphics) ?
                max + scopedDrawablePadding :
                root.height(graphics) + scopedDrawablePadding;
    }

    private static void _calculate(int top, FlowGraph graph, Graphics2D graphics, List<Drawable> drawables, List<List<Drawable>> layers) {
        if (drawables.size() == 1) {
            Drawable drawable = drawables.get(0);
            List<Drawable> predecessors = graph.predecessors(drawable);

            // Root
            if (predecessors.isEmpty()) {

                // Center in subtree
                int maxSubtreeHeight = computeSubTreeHeight(graph, drawable, graphics);
                int tmpX = X_LEFT_PADDING + sumLayerWidthForLayersBelow(findLayer(drawable, layers), layers, graphics, graph);
                int tmpY = top + Math.floorDiv(maxSubtreeHeight, 2);
                drawable.setPosition(tmpX, tmpY);

                _calculate(top, graph, graphics, graph.successors(drawable), layers);


                // Single node with one or more predecessor/s
            } else {
                int tmpX = X_LEFT_PADDING + sumLayerWidthForLayersBelow(findLayer(drawable, layers), layers, graphics, graph);
                int min = predecessors.stream().mapToInt(Drawable::y).min().getAsInt();
                int max = predecessors.stream().mapToInt(Drawable::y).max().getAsInt();
                int tmpY = Math.floorDiv(max + min, 2);

                drawable.setPosition(tmpX, tmpY);

                if (drawable instanceof ScopedDrawable) {
                    top += ScopedDrawable.VERTICAL_PADDING; // top padding
                }
                _calculate(top, graph, graphics, graph.successors(drawable), layers);
            }

            // Layer with multiple nodes.
            // Center them all in their respective subtrees.
            // Successors can be > 1 only when predecessor is ScopedDrawable
        } else if (drawables.size() > 1) {
            for (Drawable drawable : drawables) {

                // Center in subtree
                if (drawable instanceof ScopedDrawable) {
                    top += ScopedDrawable.VERTICAL_PADDING; // top padding
                }

                int tmpX = X_LEFT_PADDING + sumLayerWidthForLayersBelow(findLayer(drawable, layers), layers, graphics, graph);

                int maxSubtreeHeight = computeSubTreeHeight(graph, drawable, graphics);

                // Need to subtract the current padding since it was
                // added while computing max subtree height also.
                // The padding is added here.
                if (drawable instanceof ScopedDrawable) {
                    maxSubtreeHeight -= (ScopedDrawable.VERTICAL_PADDING + ScopedDrawable.VERTICAL_PADDING); // top and bottom
                }

                int tmpY = top + Math.floorDiv(maxSubtreeHeight, 2);
                drawable.setPosition(tmpX, tmpY);

                _calculate(top, graph, graphics, graph.successors(drawable), layers);

                if (drawable instanceof ScopedDrawable) {
                    top += ScopedDrawable.VERTICAL_PADDING; // bottom padding
                }

                top += maxSubtreeHeight;

            }
        }
    }

    private static int findLayer(Drawable current, List<List<Drawable>> layers) {
        for (int i = 0; i < layers.size(); i++) {
            if (layers.get(i).contains(current)) {
                return i;
            }
        }
        throw new RuntimeException("Could not find layer for node " + current);
    }

    private static int sumLayerWidthForLayersBelow(int layerNumber, List<List<Drawable>> layers, Graphics2D graphics, FlowGraph graph) {
        int sum = 0;
        for (int i = 0; i < layerNumber; i++) {
            sum += maxLayerWidth(i, layers, graphics, graph);
        }
        return sum;
    }

    private static int maxLayerWidth(int layerNumber, List<List<Drawable>> layers, Graphics2D graphics, FlowGraph graph) {
        List<Drawable> layerDrawables = layers.get(layerNumber);
        int max = 0;
        for (Drawable layerDrawable : layerDrawables) {
            int nestedScopes = ScopeUtilities.countNumberOfNestedScopes(graph, layerDrawable);
            int total = layerDrawable.width(graphics) + nestedScopes * ScopedDrawable.HORIZONTAL_PADDING;
            if (total > max) max = total;
        }
        return max;
    }


}
