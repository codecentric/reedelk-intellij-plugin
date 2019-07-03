package com.esb.plugin.commons;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;

import java.awt.*;
import java.util.Collection;
import java.util.Optional;

public class DesignerWindowSizeCalculator {

    private static final int WINDOW_GROW_STEP = 50;

    private DesignerWindowSizeCalculator() {
    }

    public static Optional<Dimension> from(FlowGraph graph, Graphics2D graphics) {

        // No need to adjust window size if the graph is empty.
        if (graph.isEmpty()) {
            return Optional.empty();
        }

        Collection<GraphNode> nodes = graph.nodes();

        int maxX = graph.root().x() + graph.root().width(graphics);
        int maxY = graph.root().y() + graph.root().height(graphics);

        for (GraphNode node : nodes) {
            int currentMaxX = node.x() + node.width(graphics);
            int currentMaxY = node.y() + node.bottomHalfHeight(graphics);
            if (currentMaxX > maxX) maxX = currentMaxX;
            if (currentMaxY > maxY) maxY = currentMaxY;
        }

        int newSizeX = maxX + WINDOW_GROW_STEP;
        int newSizeY = maxY + WINDOW_GROW_STEP;

        return Optional.of(new Dimension(newSizeX, newSizeY));
    }
}
