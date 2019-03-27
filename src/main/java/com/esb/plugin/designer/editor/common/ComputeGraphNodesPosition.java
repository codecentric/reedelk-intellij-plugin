package com.esb.plugin.designer.editor.common;

import com.esb.plugin.graph.handler.Drawable;
import com.google.common.graph.Graph;

import java.util.Set;

public class ComputeGraphNodesPosition {

    private static final int MIN_Y = 70;
    private static final int HORIZONTAL_PADDING = 20;

    public static void compute(Graph<Drawable> graph, Drawable root) {
        // Go to the max nodes level
        int maxNodesCount = GraphUtils.maxNodesCountAtAnyLevel(graph, root);
        int maxGraphHeight = MIN_Y + (maxNodesCount * Tile.INSTANCE.height);

        // Compute Root Position
        int centerHeight = Math.floorDiv(maxGraphHeight, 2);
        int topLeftX = Math.floorDiv(Tile.INSTANCE.width, 2);
        int topLeftY = centerHeight - Math.floorDiv(Tile.INSTANCE.height, 2);

        root.getPosition().x = topLeftX;
        root.getPosition().y = topLeftY;

        // Compute the position of the remaining
        computePosition(graph, root, topLeftX, topLeftY);
    }

    private static void computePosition(Graph graph, Drawable parent, int topLeftX, int topLeftY) {
        Set<Drawable> successors = graph.successors(parent);
        for (Drawable successor : successors) {
            int newTopLeftX = topLeftX + HORIZONTAL_PADDING + Tile.INSTANCE.width;
            int newTopLeftY = topLeftY;
            successor.getPosition().x = newTopLeftX;
            successor.getPosition().y = newTopLeftY;
            computePosition(graph, successor, newTopLeftX, newTopLeftY);
        }

    }
}
