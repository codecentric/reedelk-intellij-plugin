package de.codecentric.reedelk.plugin.commons;

import de.codecentric.reedelk.plugin.graph.node.GraphNode;

import java.awt.*;

public class LiesBetweenTopAndBottom {

    private LiesBetweenTopAndBottom() {
    }

    public static boolean of(GraphNode node, final int dropY, final Graphics2D graphics) {
        int topHalfHeight = node.topHalfHeight(graphics);
        int bottomHalfHeight = node.bottomHalfHeight(graphics);
        return dropY >= node.y() - topHalfHeight && dropY < node.y() + bottomHalfHeight;
    }
}
