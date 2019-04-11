package com.esb.plugin.designer.graph.action;

import com.esb.plugin.designer.Tile;
import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.drawable.Drawable;

import java.awt.*;

public class AddDrawableToGraphUtilities {


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
