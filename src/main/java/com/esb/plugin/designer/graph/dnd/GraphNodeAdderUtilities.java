package com.esb.plugin.designer.graph.dnd;

import com.esb.plugin.designer.Tile;
import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.drawable.Drawable;

import java.util.List;

public class GraphNodeAdderUtilities {

    public static Drawable findClosestOnYAxis(List<Drawable> precedingNodes, int dropY) {
        int min = Integer.MAX_VALUE;
        Drawable closestPrecedingNode = null;
        for (Drawable precedingNode : precedingNodes) {
            int delta = Math.abs(precedingNode.y() - dropY);
            if (delta < min) {
                closestPrecedingNode = precedingNode;
                min = delta;
            }
        }
        return closestPrecedingNode;
    }

    public static boolean withinYBounds(int dropY, Drawable node) {
        return dropY > node.y() - Tile.HALF_HEIGHT &&
                dropY < node.y() + Tile.HALF_HEIGHT;
    }

    /*
     * Checks if we are replacing the root (i.e there are no nodes preceding the drop point on X).
     * Do a check on the Y axis as well
     */
    public static boolean isReplacingRoot(FlowGraph graph, int dropX) {
        return graph
                .nodes()
                .stream()
                .noneMatch(drawable -> drawable.x() < dropX);
    }
}
