package com.esb.plugin.designer.graph.layout;

import com.esb.plugin.designer.Tile;
import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.drawable.Drawable;
import com.esb.plugin.designer.graph.drawable.ScopedDrawable;

import java.awt.*;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.esb.plugin.designer.graph.ScopeUtilities.getFirstNodeOutsideScope;
import static com.esb.plugin.designer.graph.layout.FlowGraphLayoutUtils.*;

public class FlowGraphLayout {

    private static final int X_LEFT_PADDING = Math.floorDiv(Tile.WIDTH, 2);

    public static void compute(FlowGraph graph, Graphics2D graphics) {
        FlowGraphLayers layers = new FlowGraphLayers(graph);
        List<List<Drawable>> layersList = layers.compute();
        compute(0, graph, graphics, Collections.singletonList(graph.root()), layersList);
    }

    private static void compute(int top, FlowGraph graph, Graphics2D graphics, List<Drawable> drawables, List<List<Drawable>> layers) {
        if (drawables.size() == 1) {
            Drawable drawable = drawables.get(0);
            List<Drawable> predecessors = graph.predecessors(drawable);

            // Root
            if (predecessors.isEmpty()) {

                // Center in subtree
                int maxSubtreeHeight = computeSubTreeHeight(graph, drawable, graphics);
                int tmpX = X_LEFT_PADDING + sumLayerWidthForLayersPreceding(findLayer(drawable, layers), layers, graphics, graph);
                int tmpY = top + Math.floorDiv(maxSubtreeHeight, 2);
                drawable.setPosition(tmpX, tmpY);

                compute(top, graph, graphics, graph.successors(drawable), layers);


                // Single node with one or more predecessor/s
            } else {
                int tmpX = X_LEFT_PADDING + sumLayerWidthForLayersPreceding(findLayer(drawable, layers), layers, graphics, graph);
                int min = predecessors.stream().mapToInt(Drawable::y).min().getAsInt();
                int max = predecessors.stream().mapToInt(Drawable::y).max().getAsInt();
                int tmpY = Math.floorDiv(max + min, 2);

                drawable.setPosition(tmpX, tmpY);

                if (drawable instanceof ScopedDrawable) {
                    top += ScopedDrawable.VERTICAL_PADDING; // top padding
                }
                compute(top, graph, graphics, graph.successors(drawable), layers);
            }

            // Layer with multiple nodes.
            // Center them all in their respective subtrees.
            // Successors can be > 1 only when predecessor is ScopedDrawable
        } else if (drawables.size() > 1) {

            Drawable commonParent = findCommonParent(graph, drawables);
            Optional<Drawable> firstDrawableOutsideScope = getFirstNodeOutsideScope(graph, (ScopedDrawable) commonParent);

            int maxSubTreeHeight = computeMaxHeight(graphics, graph, commonParent, firstDrawableOutsideScope, 0);

            top = commonParent.y() - Math.floorDiv(maxSubTreeHeight, 2);

            // Need to find max height until the end of the scope

            for (Drawable drawable : drawables) {

                // Center in subtree
                if (drawable instanceof ScopedDrawable) {
                    top += ScopedDrawable.VERTICAL_PADDING; // top padding
                }

                int tmpX = X_LEFT_PADDING + sumLayerWidthForLayersPreceding(findLayer(drawable, layers), layers, graphics, graph);

                int maxSubtreeHeight = computeMaxHeight(graphics, graph, drawable, firstDrawableOutsideScope, 0);

                // Need to subtract the current padding since it was
                // added while computing max subtree height also.
                // The padding is added here.
                if (drawable instanceof ScopedDrawable) {
                    //     maxSubtreeHeight -= (ScopedDrawable.VERTICAL_PADDING + ScopedDrawable.VERTICAL_PADDING); // top and bottom
                }

                int tmpY = top + Math.floorDiv(maxSubtreeHeight, 2);
                drawable.setPosition(tmpX, tmpY);

                compute(top, graph, graphics, graph.successors(drawable), layers);

                if (drawable instanceof ScopedDrawable) {
                    top += ScopedDrawable.VERTICAL_PADDING; // bottom padding
                }

                top += maxSubtreeHeight;

            }
        }
    }

}
