package com.esb.plugin.designer.graph;

import com.esb.plugin.designer.Tile;
import com.esb.plugin.designer.graph.drawable.Drawable;

import java.awt.*;
import java.util.List;
import java.util.Optional;

import static com.esb.plugin.designer.graph.Predicates.byPrecedingNodesOnX;
import static java.util.stream.Collectors.toList;

public class AddDrawableToGraphUtilities {

    /*
     * Finds the closest preceding (on X axis) drawable and closest on Y axis.
     */
    public static Optional<Drawable> findClosestPrecedingDrawable(FlowGraph graph, Point dropPoint) {
        // Find all preceding nodes on X axis
        List<Drawable> precedingNodes = graph
                .nodes()
                .stream()
                .filter(byPrecedingNodesOnX(graph, dropPoint.x))
                .collect(toList());
        return findClosestOnYAxis(precedingNodes, dropPoint.y);
    }

    private static Optional<Drawable> findClosestOnYAxis(List<Drawable> precedingNodes, int dropY) {
        int min = Integer.MAX_VALUE;
        Drawable closestPrecedingNode = null;
        for (Drawable precedingNode : precedingNodes) {
            int delta = Math.abs(precedingNode.y() - dropY);
            if (delta < min) {
                closestPrecedingNode = precedingNode;
                min = delta;
            }
        }
        return Optional.ofNullable(closestPrecedingNode);
    }

    public static boolean withinYBounds(int dropY, Drawable node) {
        return dropY > node.y() - Tile.HALF_HEIGHT &&
                dropY < node.y() + Tile.HALF_HEIGHT;
    }

    /*
     * Checks if we are replacing the root (i.e there are no nodes preceding the drop point on X).
     * Do a check on the Y axis as well
     */
    public static boolean isReplacingRoot(FlowGraph graph, Point dropPoint) {
        return graph
                .nodes()
                .stream()
                .noneMatch(drawable -> drawable.x() < dropPoint.x);
    }
}
