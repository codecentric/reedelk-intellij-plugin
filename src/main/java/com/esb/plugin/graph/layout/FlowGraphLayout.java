package com.esb.plugin.graph.layout;

import com.esb.plugin.commons.Half;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.layout.utils.ComputeLayerWidthSumPreceding;
import com.esb.plugin.graph.layout.utils.ComputeMaxHeight;
import com.esb.plugin.graph.layout.utils.FindCommonParent;
import com.esb.plugin.graph.layout.utils.FindContainingLayer;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.ScopeBoundaries;
import com.esb.plugin.graph.node.ScopedGraphNode;
import com.esb.plugin.graph.utils.FindFirstNodeOutsideScope;
import com.esb.plugin.graph.utils.FindJoiningScope;
import com.esb.plugin.graph.utils.FindMaxBottomHalfHeight;
import com.esb.plugin.graph.utils.FindTopHalfHeight;

import java.awt.*;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.esb.plugin.graph.node.ScopedGraphNode.VERTICAL_PADDING;
import static com.google.common.base.Preconditions.checkState;

public class FlowGraphLayout {

    public static void compute(FlowGraph graph, Graphics2D graphics) {
        compute(graph, graphics, 0);
    }

    public static void compute(FlowGraph graph, Graphics2D graphics, int topPadding) {

        FlowGraphLayers layers = new FlowGraphLayers(graph);

        List<List<GraphNode>> layersList = layers.compute();

        if (!graph.isEmpty()) {

            List<GraphNode> currentNodesToProcess = Collections.singletonList(graph.root());

            compute(topPadding, graph, graphics, currentNodesToProcess, layersList);

        }
    }

    private static void compute(int top, FlowGraph graph, Graphics2D graphics, List<GraphNode> nodes, List<List<GraphNode>> layers) {
        if (nodes.size() == 1) {
            GraphNode node = nodes.get(0);
            List<GraphNode> predecessors = graph.predecessors(node);

            // Root
            if (predecessors.isEmpty()) {

                // Compute new X coordinate
                int containingLayerIndex = FindContainingLayer.of(layers, node);
                int XCoordinate = Half.of(node.width(graphics)) +
                        ComputeLayerWidthSumPreceding.of(graph, graphics, layers, containingLayerIndex);

                // Compute new Y coordinate
                int topHalfHeight = FindTopHalfHeight.of(graph, graphics, node, null, node.topHalfHeight(graphics));
                int YCoordinate = top + topHalfHeight;

                node.setPosition(XCoordinate, YCoordinate);

                compute(top, graph, graphics, graph.successors(node), layers);


                // Single node with one or more predecessor/s
            } else {

                // Find layer containing this node
                int containingLayerIndex = FindContainingLayer.of(layers, node);
                int XCoordinate = Half.of(node.width(graphics)) +
                        ComputeLayerWidthSumPreceding.of(graph, graphics, layers, containingLayerIndex);

                // Predecessors must not be empty
                int min = predecessors.stream().mapToInt(GraphNode::y).min().getAsInt();
                int max = predecessors.stream().mapToInt(GraphNode::y).max().getAsInt();

                // If this node is joining a scope, then we place it in the
                // center of the scope this node is joining to.
                Optional<ScopedGraphNode> scopeItIsJoining = FindJoiningScope.of(graph, node);
                if (scopeItIsJoining.isPresent()) {
                    ScopedGraphNode scope = scopeItIsJoining.get();
                    ScopeBoundaries scopeBoundaries = scope.getScopeBoundaries(graph, graphics);
                    min = scope.y() - Half.of(scopeBoundaries.getHeight());
                    max = scope.y() + Half.of(scopeBoundaries.getHeight());
                }

                int YCoordinate = Half.of(max + min);

                node.setPosition(XCoordinate, YCoordinate);

                if (node instanceof ScopedGraphNode) {
                    top += VERTICAL_PADDING; // top padding
                }

                compute(top, graph, graphics, graph.successors(node), layers);
            }


        } else if (nodes.size() > 1) {
            // Layer with multiple nodes: center them all in their respective subtrees.
            // Successors can be > 1 only when predecessor is ScopedGraphNode
            GraphNode commonParent = FindCommonParent.of(graph, nodes);

            // The common parent must be a scoped node since only
            // scoped nodes can have more than one successor
            checkState(commonParent instanceof ScopedGraphNode);

            Optional<GraphNode> optionalFirstNodeOutsideScope = FindFirstNodeOutsideScope.of(graph, (ScopedGraphNode) commonParent);
            GraphNode firstNodeOutsideScope = optionalFirstNodeOutsideScope.orElse(null);

            int maxSubTreeHeight = ComputeMaxHeight.of(graph, graphics, commonParent, firstNodeOutsideScope);

            // Vertical padding because it is inside a scope
            top = VERTICAL_PADDING + commonParent.y() - Half.of(maxSubTreeHeight);


            for (GraphNode node : nodes) {

                // If the node it is a scope, we need to add Padding
                if (node instanceof ScopedGraphNode) {
                    top += VERTICAL_PADDING;
                }

                // Compute new X coordinate
                int containingLayerIndex = FindContainingLayer.of(layers, node);
                int XCoordinate = Half.of(node.width(graphics)) +
                        ComputeLayerWidthSumPreceding.of(graph, graphics, layers, containingLayerIndex);

                // Compute new Y coordinate: the top half height is needed since there might
                // be nodes with a longer bottom half, for instance when a description is very long.
                int maxTopHalfHeight = FindTopHalfHeight.of(graph, graphics, node, firstNodeOutsideScope, node.topHalfHeight(graphics));
                int YCoordinate = top + maxTopHalfHeight;

                node.setPosition(XCoordinate, YCoordinate);

                // Recursively assign position to other successors of current node
                compute(top, graph, graphics, graph.successors(node), layers);

                // The new top is the tallest bottom half until the first node outside the scope
                top = YCoordinate + FindMaxBottomHalfHeight.of(graph, graphics, node, firstNodeOutsideScope);
            }
        }
    }
}
