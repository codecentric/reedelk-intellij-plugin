package de.codecentric.reedelk.plugin.graph.layout;

import de.codecentric.reedelk.plugin.graph.node.GraphNode;
import de.codecentric.reedelk.plugin.graph.node.ScopedGraphNode;

import java.awt.*;
import java.util.List;

public class ComputeLayerWidthSumPreceding {

    private ComputeLayerWidthSumPreceding() {
    }

    public static int of(Graphics2D graphics, List<List<GraphNode>> layers, int precedingLayerIndex, ComputeMaxScopesEndingInEachLayer maxScopesEndingInEachLayer) {
        int sum = 0;
        for (int i = 0; i < precedingLayerIndex; i++) {
            List<GraphNode> layerDrawables = layers.get(i);
            int maxNumberOfNestedScopesEndingInLayerIndex = maxScopesEndingInEachLayer.forLayer(i);
            sum = maxLayerWidth(graphics, layerDrawables, sum, maxNumberOfNestedScopesEndingInLayerIndex);
        }
        return sum;
    }

    private static int maxLayerWidth(Graphics2D graphics, List<GraphNode> layerNodes, int currentSum, int maxScopes) {
        int max = currentSum;
        for (GraphNode layerNode : layerNodes) {
            int total = layerNode.width(graphics) + (maxScopes * ScopedGraphNode.HORIZONTAL_PADDING);
            if (total + currentSum > max) max = total + currentSum;
        }
        return max;
    }
}
