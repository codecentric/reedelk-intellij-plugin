package com.esb.plugin.graph.layout;

import com.esb.plugin.designer.Tile;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.ScopeBoundaries;
import com.esb.plugin.graph.node.ScopedDrawable;
import com.esb.plugin.graph.scope.FindFirstNodeOutsideScope;
import com.esb.plugin.graph.scope.FindJoiningScope;

import java.awt.*;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.esb.plugin.graph.layout.FlowGraphLayoutUtils.*;
import static com.esb.plugin.graph.node.ScopedDrawable.VERTICAL_PADDING;
import static com.google.common.base.Preconditions.checkState;

public class FlowGraphLayout {

    private static final int X_LEFT_PADDING = Math.floorDiv(Tile.WIDTH, 2);

    public static void compute(FlowGraph graph, Graphics2D graphics) {
        FlowGraphLayers layers = new FlowGraphLayers(graph);
        List<List<GraphNode>> layersList = layers.compute();
        compute(0, graph, graphics, Collections.singletonList(graph.root()), layersList);
    }

    private static void compute(int top, FlowGraph graph, Graphics2D graphics, List<GraphNode> nodes, List<List<GraphNode>> layers) {
        if (nodes.size() == 1) {
            GraphNode node = nodes.get(0);
            List<GraphNode> predecessors = graph.predecessors(node);

            // Root
            if (predecessors.isEmpty()) {

                // Find layer containing this node
                int containingLayerIndex = findContainingLayer(layers, node);

                // Center in subtree
                int maxSubtreeHeight = FlowGraphLayoutUtils.maxHeight(graph, graphics, node);

                int tmpX = X_LEFT_PADDING + layerWidthSumPreceding(graph, graphics, layers, containingLayerIndex);
                int tmpY = top + Math.floorDiv(maxSubtreeHeight, 2);
                node.setPosition(tmpX, tmpY);

                compute(top, graph, graphics, graph.successors(node), layers);


                // Single node with one or more predecessor/s
            } else {

                // Find layer containing this node
                int containingLayerIndex = findContainingLayer(layers, node);

                int tmpX = X_LEFT_PADDING + layerWidthSumPreceding(graph, graphics, layers, containingLayerIndex);

                // If it is the first node outside a scope, center it in the middle of the scope
                // this node is joining from.

                // Otherwise take min and max.
                Optional<ScopedDrawable> scopeItIsJoining = FindJoiningScope.of(graph, node);

                int min = predecessors.stream().mapToInt(GraphNode::y).min().getAsInt();
                int max = predecessors.stream().mapToInt(GraphNode::y).max().getAsInt();

                // TODO: Test this logic properly and optimize the code.
                // If this node is joining a scope, then we place it in the
                // center of the scope this node is joining to.
                if (scopeItIsJoining.isPresent()) {
                    ScopedDrawable scope = scopeItIsJoining.get();
                    ScopeBoundaries scopeBoundaries = scope.getScopeBoundaries(graph, graphics);
                    min = scope.y() - Math.floorDiv(scopeBoundaries.getHeight(), 2);
                    max = scope.y() + Math.floorDiv(scopeBoundaries.getHeight(), 2);
                }

                int tmpY = Math.floorDiv(max + min, 2);

                node.setPosition(tmpX, tmpY);

                if (node instanceof ScopedDrawable) {
                    top += VERTICAL_PADDING; // top padding
                }

                compute(top, graph, graphics, graph.successors(node), layers);
            }


        } else if (nodes.size() > 1) {
            // Layer with multiple nodes.
            // Center them all in their respective subtrees.
            // Successors can be > 1 only when predecessor is ScopedDrawable
            GraphNode commonParent = findCommonParent(graph, nodes); // common parent must be (scoped node)

            checkState(commonParent instanceof ScopedDrawable);

            Optional<GraphNode> optionalFirstDrawableOutsideScope = FindFirstNodeOutsideScope.of(graph, (ScopedDrawable) commonParent);
            GraphNode firstDrawableOutsideScope = optionalFirstDrawableOutsideScope.orElse(null);

            int maxSubTreeHeight = FlowGraphLayoutUtils.maxHeight(graph, graphics, commonParent, firstDrawableOutsideScope);

            top = VERTICAL_PADDING + commonParent.y() - Math.floorDiv(maxSubTreeHeight, 2);


            for (GraphNode node : nodes) {

                // Find layer containing this node
                int containingLayerIndex = findContainingLayer(layers, node);

                // Center in subtree
                if (node instanceof ScopedDrawable) {
                    top += VERTICAL_PADDING; // top padding
                }

                int tmpX = X_LEFT_PADDING + layerWidthSumPreceding(graph, graphics, layers, containingLayerIndex);

                int maxSubtreeHeight = FlowGraphLayoutUtils.maxHeight(graph, graphics, node, firstDrawableOutsideScope);

                // We must subtract the current padding since it
                // was added while computing max subtree height as well.
                if (node instanceof ScopedDrawable) {
                    maxSubtreeHeight -= (VERTICAL_PADDING + VERTICAL_PADDING); // top and bottom
                }

                int tmpY = top + Math.floorDiv(maxSubtreeHeight, 2);
                node.setPosition(tmpX, tmpY);

                compute(top, graph, graphics, graph.successors(node), layers);

                if (node instanceof ScopedDrawable) {
                    top += VERTICAL_PADDING; // bottom padding
                }

                top += maxSubtreeHeight;

            }
        }
    }

}
