package com.esb.plugin.graph.layout.utils;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.utils.CountNestedScopes;

import java.awt.*;
import java.util.List;

import static com.esb.plugin.graph.node.ScopedGraphNode.HORIZONTAL_PADDING;

public class ComputeLayerWidthSumPreceding {

    public static int of(FlowGraph graph, Graphics2D graphics, List<List<GraphNode>> layers, int precedingLayerIndex) {
        int sum = 0;
        for (int i = 0; i < precedingLayerIndex; i++) {
            java.util.List<GraphNode> layerDrawables = layers.get(i);
            sum += maxLayerWidth(graph, graphics, layerDrawables);
        }
        return sum;
    }

    private static int maxLayerWidth(FlowGraph graph, Graphics2D graphics, List<GraphNode> layerNodes) {
        int max = 0;
        for (GraphNode layerNode : layerNodes) {
            int nestedScopes = CountNestedScopes.of(graph, layerNode);
            int total = layerNode.width(graphics) + nestedScopes * HORIZONTAL_PADDING;
            if (total > max) max = total;
        }
        return max;
    }
}
