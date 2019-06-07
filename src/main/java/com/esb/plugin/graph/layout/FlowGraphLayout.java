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

import java.awt.*;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.esb.plugin.graph.node.ScopedGraphNode.VERTICAL_PADDING;
import static com.google.common.base.Preconditions.checkState;

public class FlowGraphLayout {

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
                int containingLayerIndex = FindContainingLayer.of(layers, node);

                // Center in subtree
                int maxSubtreeHeight = ComputeMaxHeight.of(graph, graphics, node);

                int tmpX = Half.of(node.width(graphics)) + ComputeLayerWidthSumPreceding.of(graph, graphics, layers, containingLayerIndex);
                int tmpY = top + Half.of(maxSubtreeHeight);
                node.setPosition(tmpX, tmpY);

                compute(top, graph, graphics, graph.successors(node), layers);


                // Single node with one or more predecessor/s
            } else {

                // Find layer containing this node
                int containingLayerIndex = FindContainingLayer.of(layers, node);

                int tmpX = Half.of(node.width(graphics)) +
                        ComputeLayerWidthSumPreceding.of(graph, graphics, layers, containingLayerIndex);

                // If it is the first node outside a scope, center it in the middle of the scope
                // this node is joining from.

                // Otherwise take min and max.
                Optional<ScopedGraphNode> scopeItIsJoining = FindJoiningScope.of(graph, node);

                // TODO: what if int not present?!
                int min = predecessors.stream().mapToInt(GraphNode::y).min().getAsInt();
                int max = predecessors.stream().mapToInt(GraphNode::y).max().getAsInt();

                // If this node is joining a scope, then we place it in the
                // center of the scope this node is joining to.
                if (scopeItIsJoining.isPresent()) {
                    ScopedGraphNode scope = scopeItIsJoining.get();
                    ScopeBoundaries scopeBoundaries = scope.getScopeBoundaries(graph, graphics);
                    min = scope.y() - Half.of(scopeBoundaries.getHeight());
                    max = scope.y() + Half.of(scopeBoundaries.getHeight());
                }

                int tmpY = Half.of(max + min);

                node.setPosition(tmpX, tmpY);

                if (node instanceof ScopedGraphNode) {
                    top += VERTICAL_PADDING; // top padding
                }

                compute(top, graph, graphics, graph.successors(node), layers);
            }


        } else if (nodes.size() > 1) {
            // Layer with multiple nodes.
            // Center them all in their respective subtrees.
            // Successors can be > 1 only when predecessor is ScopedGraphNode
            GraphNode commonParent = FindCommonParent.of(graph, nodes); // common parent must be (scoped node)

            checkState(commonParent instanceof ScopedGraphNode);

            Optional<GraphNode> optionalFirstNodeOutsideScope = FindFirstNodeOutsideScope.of(graph, (ScopedGraphNode) commonParent);
            GraphNode firstNodeOutsideScope = optionalFirstNodeOutsideScope.orElse(null);

            int maxSubTreeHeight = ComputeMaxHeight.of(graph, graphics, commonParent, firstNodeOutsideScope);

            top = VERTICAL_PADDING + commonParent.y() - Half.of(maxSubTreeHeight);


            for (GraphNode node : nodes) {

                // Find layer containing this node
                int containingLayerIndex = FindContainingLayer.of(layers, node);

                // Center in subtree
                if (node instanceof ScopedGraphNode) {
                    top += VERTICAL_PADDING; // top padding
                }

                int tmpX = Half.of(node.width(graphics)) +
                        ComputeLayerWidthSumPreceding.of(graph, graphics, layers, containingLayerIndex);

                int maxSubtreeHeight = ComputeMaxHeight.of(graph, graphics, node, firstNodeOutsideScope);

                // We must subtract the current padding since it
                // was added while computing max subtree height as well.
                if (node instanceof ScopedGraphNode) {
                    maxSubtreeHeight -= (VERTICAL_PADDING + VERTICAL_PADDING); // top and bottom
                }

                int tmpY = top + Half.of(maxSubtreeHeight);
                node.setPosition(tmpX, tmpY);

                compute(top, graph, graphics, graph.successors(node), layers);

                if (node instanceof ScopedGraphNode) {
                    top += VERTICAL_PADDING; // bottom padding
                }

                top += maxSubtreeHeight;
            }
        }
    }
}
