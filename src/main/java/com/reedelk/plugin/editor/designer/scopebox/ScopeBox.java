package com.reedelk.plugin.editor.designer.scopebox;

import com.intellij.ui.JBColor;
import com.reedelk.plugin.commons.Half;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.layout.ComputeMaxHeight;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.graph.node.ScopeBoundaries;
import com.reedelk.plugin.graph.node.ScopedGraphNode;
import com.reedelk.plugin.graph.utils.CountMaxScopes;
import com.reedelk.plugin.graph.utils.FindFirstNodeOutsideScope;
import com.reedelk.plugin.graph.utils.FindScopes;
import com.reedelk.plugin.graph.utils.ListLastNodesOfScope;

import java.awt.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

// Scope Box
//  ----------------
//  |          C5  |
//  |     C2       |
//    S1           |
//  |     C3       |
//  |          C4  |
//  ----------------
@SuppressWarnings("UnnecessaryLocalVariable")
public abstract class ScopeBox {

    private static final int LEFT_PADDING = 3;
    private static final int MID_TOP_LEFT_PADDING = 20;
    private static final int MID_BOTTOM_LEFT_PADDING = 30;
    private static final int IN_BETWEEN_SCOPES_PADDING = 5;

    private Stroke stroke;
    private JBColor boundariesColor;

    ScopeBox(JBColor boundariesColor, Stroke stroke) {
        this.stroke = stroke;
        this.boundariesColor = boundariesColor;
    }

    public void draw(FlowGraph graph, Graphics2D graphics, ScopedGraphNode node) {
        graphics.setStroke(stroke);
        graphics.setColor(boundariesColor);

        ScopeBoundaries boundaries = getBoundaries(graph, graphics, node);
        int x = boundaries.getX();
        int y = boundaries.getY();

        int topLeftX = x + LEFT_PADDING;
        int topLeftY = y;
        int topRightX = x + boundaries.getWidth();
        int topRightY = y;
        int bottomRightX = x + boundaries.getWidth();
        int bottomRightY = y + boundaries.getHeight();
        int bottomLeftX = x + LEFT_PADDING;
        int bottomLeftY = y + boundaries.getHeight();

        int midBottomLeftX = x + LEFT_PADDING;
        int midBottomLeftY = node.getTargetArrowEnd().y + MID_BOTTOM_LEFT_PADDING;
        int midTopLeftX = x + LEFT_PADDING;
        int midTopLeftY = node.getTargetArrowEnd().y - MID_TOP_LEFT_PADDING;

        graphics.drawLine(topLeftX, topLeftY, topRightX, topRightY);
        graphics.drawLine(topRightX, topLeftY, bottomRightX, bottomRightY);
        graphics.drawLine(bottomRightX, bottomRightY, bottomLeftX, bottomLeftY);

        graphics.drawLine(bottomLeftX, bottomLeftY, midBottomLeftX, midBottomLeftY);
        graphics.drawLine(midTopLeftX, midTopLeftY, topLeftX, topLeftY);
    }

    public ScopeBoundaries getBoundaries(FlowGraph graph, Graphics2D graphics, ScopedGraphNode scopedGraphNode) {
        NodesOnBoundaries boundaries = findNodesOnBoundaries(graph, scopedGraphNode);

        GraphNode firstNodeOutsideScope = FindFirstNodeOutsideScope.of(graph, scopedGraphNode).orElse(null);

        int subTreeHeight = ComputeMaxHeight.of(graph, graphics, scopedGraphNode, firstNodeOutsideScope);

        int halfSubTreeHeight = Half.of(subTreeHeight);

        int minY = scopedGraphNode.y() - halfSubTreeHeight + ScopedGraphNode.VERTICAL_PADDING;
        int maxY = scopedGraphNode.y() + halfSubTreeHeight - ScopedGraphNode.VERTICAL_PADDING;

        // Draw Scope Boundaries we need to compute the maximum number
        // of nested scopes belonging to the right-most node (the one with Max X coordinate).
        int maxScopes = CountMaxScopes.of(scopedGraphNode, boundaries.maxX);

        int minX = boundaries.minX.x() - Half.of(boundaries.minX.width(graphics));
        int maxX = boundaries.maxX.x() + Half.of(boundaries.maxX.width(graphics)) + (maxScopes * IN_BETWEEN_SCOPES_PADDING);

        int width = maxX - minX;
        int height = maxY - minY;

        return new ScopeBoundaries(minX, minY, width, height);
    }

    static class NodesOnBoundaries {
        GraphNode maxX;
        GraphNode minX;
        GraphNode maxY;
        GraphNode minY;

        private NodesOnBoundaries() {
        }
    }

    static NodesOnBoundaries findNodesOnBoundaries(FlowGraph graph, ScopedGraphNode scopedGraphNode) {
        Collection<GraphNode> nodes = ListLastNodesOfScope.from(graph, scopedGraphNode);

        NodesOnBoundaries boundaries = new NodesOnBoundaries();
        boundaries.maxX = scopedGraphNode;
        boundaries.minX = scopedGraphNode;
        boundaries.maxY = scopedGraphNode;
        boundaries.minY = scopedGraphNode;

        // If there are no nodes, it means that it
        // is a ScopedNode without nodes in it.
        if (nodes.isEmpty()) return boundaries;

        Set<GraphNode> allNodes = new HashSet<>(nodes);
        allNodes.add(scopedGraphNode);

        // We need to find min x, max x, min y and max y
        for (GraphNode currentNode : allNodes) {

            if (boundaries.maxX.x() <= currentNode.x()) {
                // If the current node has the same x coordinate of the one having
                // max X, then we need to take the one belonging to the maximum number
                // of scopes between the two. This is because down below we compute the
                // max X of this ScopedGraphNode by multiplying max number of
                // scopes * IN_BETWEEN_SCOPES_PADDING the max number of scopes is computed
                // starting from the node with max X.
                if (boundaries.maxX.x() == currentNode.x()) {
                    int scopesOfCurrent = FindScopes.of(graph, currentNode).size();
                    int scopesOfNodeWithMaxX = FindScopes.of(graph, boundaries.maxX).size();
                    if (scopesOfCurrent > scopesOfNodeWithMaxX) {
                        boundaries.maxX = currentNode;
                    }
                } else {
                    boundaries.maxX = currentNode;
                }
            }

            if (boundaries.minX.x() > currentNode.x()) {
                boundaries.minX = currentNode;
            }
            if (boundaries.maxY.y() < currentNode.y()) {
                boundaries.maxY = currentNode;
            }
            if (boundaries.minY.y() > currentNode.y()) {
                boundaries.minY = currentNode;
            }
        }

        return boundaries;
    }
}
