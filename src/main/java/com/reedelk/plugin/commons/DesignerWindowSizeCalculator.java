package com.reedelk.plugin.commons;

import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.graph.utils.FindMaxBottomHalfHeight;

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

        int maxBottomHalf = FindMaxBottomHalfHeight.of(graph, graphics, graph.root(), null);
        int maxY = graph.root().y() + maxBottomHalf;

        int maxX = graph.root().x() + graph.root().width(graphics);
        Collection<GraphNode> endNodes = graph.endNodes();
        for (GraphNode endNode : endNodes) {
            int currentX = endNode.x() + endNode.width(graphics);
            if (currentX > maxX) {
                maxX = currentX;
            }
        }

        int newSizeX = maxX + WINDOW_GROW_STEP;
        int newSizeY = maxY + WINDOW_GROW_STEP;

        return Optional.of(new Dimension(newSizeX, newSizeY));
    }
}
