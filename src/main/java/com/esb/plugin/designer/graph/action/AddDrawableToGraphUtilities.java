package com.esb.plugin.designer.graph.action;

import com.esb.plugin.designer.Tile;
import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.drawable.Drawable;

import java.awt.*;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

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
        return findClosestOnYAxis(precedingNodes, dropPoint.y, dropPoint.x);
    }

    // If there are two on the same Y we pick the closest on X
    private static Optional<Drawable> findClosestOnYAxis(List<Drawable> precedingNodes, int dropY, int dropX) {
        int minY = Integer.MAX_VALUE;
        int minX = Integer.MAX_VALUE;
        Drawable closestPrecedingNode = null;
        for (Drawable precedingNode : precedingNodes) {
            int delta = Math.abs(precedingNode.y() - dropY);
            if (delta < minY) {
                closestPrecedingNode = precedingNode;
                minY = delta;
                minX = precedingNode.x();
            } else if (delta == minY) {
                if (Math.abs(dropX - precedingNode.x()) < Math.abs(minX - dropX)) {
                    closestPrecedingNode = precedingNode;
                    minX = precedingNode.x();
                }
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

    private static Predicate<Drawable> byPrecedingNodesOnX(FlowGraph graph, int dropX) {
        return preceding -> {
            // The drop point is before/after the center of the node or the center + next node position.
            if (dropX <= preceding.x() || dropX >= preceding.x() + Tile.WIDTH + Tile.HALF_WIDTH) {
                return false;
            }
            // If exists a successor of the current preceding preceding in the preceding + 1 position,
            // then we restrict the drop position so that we consider valid if and only if its x
            // coordinates are between preceding x and successor x.
            for (Drawable successor : graph.successors(preceding)) {
                if (successor.x() == preceding.x() + Tile.WIDTH) {
                    return dropX > preceding.x() && dropX < successor.x();
                }
            }
            // The next successor is beyond the next position so we consider valid a drop point
            // between preceding x and until the end of preceding + 1 position
            return true;
        };
    }
}
