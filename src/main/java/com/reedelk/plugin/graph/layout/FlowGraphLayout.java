package com.reedelk.plugin.graph.layout;

import com.reedelk.plugin.commons.Half;
import com.reedelk.plugin.commons.IsPredecessorScopedNode;
import com.reedelk.plugin.commons.IsScopedGraphNode;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.graph.node.ScopeBoundaries;
import com.reedelk.plugin.graph.node.ScopedGraphNode;
import com.reedelk.plugin.graph.utils.*;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkState;
import static com.reedelk.plugin.graph.node.ScopedGraphNode.VERTICAL_PADDING;

public class FlowGraphLayout {

    private static final GraphNode UNTIL_NO_SUCCESSORS = null;

    private final FlowGraph graph;
    private final Graphics2D graphics;
    private final List<List<GraphNode>> layersList;
    private final ComputeMaxScopesEndingInEachLayer scopesCountByLayer;


    private FlowGraphLayout(@NotNull FlowGraph graph, @NotNull Graphics2D graphics) {
        this.graph = graph;
        this.graphics = graphics;

        FlowGraphLayers layersCalculator = new FlowGraphLayers(graph);
        this.layersList = layersCalculator.compute();
        this.scopesCountByLayer = new ComputeMaxScopesEndingInEachLayer(graph, layersList);
    }

    public static void compute(FlowGraph graph, Graphics2D graphics, int topPadding) {
        if (!graph.isEmpty()) {
            FlowGraphLayout flowGraphLayout = new FlowGraphLayout(graph, graphics);
            List<GraphNode> currentNodesToProcess = Collections.singletonList(graph.root());
            flowGraphLayout.compute(topPadding, currentNodesToProcess);
        }
    }

    private void compute(int top, List<GraphNode> nodes) {
        if (nodes.isEmpty()) {
            // Nothing to compute
            return;
        }

        // If layer following a scoped node.
        if (isLayerFollowingScopedNode(graph, nodes)) {

            computeLayerFollowingScopedNode(graph, graphics, nodes, layersList);

            // It is not a layer following a scoped node
        } else {
            GraphNode node = nodes.get(0);

            List<GraphNode> predecessors = graph.predecessors(node);

            // Root
            if (predecessors.isEmpty()) {

                computeXAndY(top, graph, graphics, layersList, node, UNTIL_NO_SUCCESSORS);

                compute(top, graph.successors(node));

                // Single node with one or more predecessor/s
            } else {

                // Find layer containing this node
                int containingLayerIndex = FindContainingLayer.of(layersList, node);
                int xCoordinate = Half.of(node.width(graphics)) +
                        ComputeLayerWidthSumPreceding.of(graphics, layersList, containingLayerIndex, scopesCountByLayer);

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

                int yCoordinate = Half.of(max + min);

                node.setPosition(xCoordinate, yCoordinate);

                if (IsScopedGraphNode.of(node)) {
                    top += VERTICAL_PADDING; // top padding
                }

                compute(top, graph.successors(node));
            }
        }
    }

    private void computeLayerFollowingScopedNode(FlowGraph graph, Graphics2D graphics, List<GraphNode> nodes, List<List<GraphNode>> layers) {
        // Successors can be > 1 only when predecessor is ScopedGraphNode
        GraphNode commonParent = FindCommonParent.of(graph, nodes);

        // The common parent must be a scoped node since only
        // scoped nodes can have more than one successor
        checkState(IsScopedGraphNode.of(commonParent));

        Optional<GraphNode> optionalFirstNodeOutsideScope = FindFirstNodeOutsideScope.of(graph, (ScopedGraphNode) commonParent);
        GraphNode firstNodeOutsideScope = optionalFirstNodeOutsideScope.orElse(null);

        int maxSubTreeHeight = ComputeMaxHeight.of(graph, graphics, commonParent, firstNodeOutsideScope);

        // Vertical padding because it is inside a scope
        int top = VERTICAL_PADDING + commonParent.y() - Half.of(maxSubTreeHeight);


        for (GraphNode node : nodes) {

            computeXAndY(top, graph, graphics, layers, node, firstNodeOutsideScope);

            // Recursively assign position to other successors of current node
            compute(top, graph.successors(node));

            // The new top is the tallest bottom half until the first node outside the scope
            top = node.y() + FindMaxBottomHalfHeight.of(graph, graphics, node, firstNodeOutsideScope);
        }
    }

    private void computeXAndY(int top, FlowGraph graph, Graphics2D graphics, List<List<GraphNode>> layers, GraphNode node, GraphNode stop) {
        // Compute new X coordinate
        int containingLayerIndex = FindContainingLayer.of(layers, node);
        int XCoordinate = Half.of(node.width(graphics)) +
                ComputeLayerWidthSumPreceding.of(graphics, layers, containingLayerIndex, scopesCountByLayer);

        // Compute new Y coordinate
        int topHalfHeight = FindMaxTopHalfHeight.of(graph, graphics, node, stop);
        int YCoordinate = top + topHalfHeight;

        node.setPosition(XCoordinate, YCoordinate);
    }

    private boolean isLayerFollowingScopedNode(FlowGraph graph, List<GraphNode> nodes) {
        if (nodes.isEmpty()) {
            return false;
        }
        List<GraphNode> predecessor = graph.predecessors(nodes.get(0));
        return IsPredecessorScopedNode.of(predecessor);
    }
}
