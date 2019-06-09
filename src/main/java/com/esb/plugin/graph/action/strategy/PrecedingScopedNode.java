package com.esb.plugin.graph.action.strategy;

import com.esb.plugin.commons.Half;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.ScopedGraphNode;
import com.esb.plugin.graph.utils.FindFirstNodeOutsideScope;

import java.awt.*;
import java.util.List;

import static com.google.common.base.Preconditions.checkState;

/**
 * Strategy which can be applied to preceding nodes of type ScopedGraphNode.
 * Only a scoped node might have more than one successor.
 */
public class PrecedingScopedNode extends AbstractStrategy {

    public PrecedingScopedNode(FlowGraph graph, Point dropPoint, GraphNode node, Graphics2D graphics) {
        super(graph, dropPoint, node, graphics);
    }

    // It is the only type of node with potentially many successors.
    @Override
    public void execute(GraphNode scopeNode) {
        checkState(scopeNode instanceof ScopedGraphNode,
                "Strategy only accepts ScopedGraphNode");

        ScopedGraphNode closestPrecedingNode = (ScopedGraphNode) scopeNode;

        List<GraphNode> successors = graph.successors(closestPrecedingNode);

        int scopeMaxXBound = getScopeMaxXBound(graph, closestPrecedingNode, graphics);

        if (successors.isEmpty()) {
            graph.add(closestPrecedingNode, node);
            // We just add the node to the scope if it is within the scope max X bounds
            if (dropPoint.x < scopeMaxXBound) {
                closestPrecedingNode.addToScope(node);
            }
            return;
        }

        // We handle the case where the successor is only one and it is outside the scope
        if (successors.size() == 1 && !closestPrecedingNode.scopeContains(successors.get(0))) {
            GraphNode successorOfClosestPrecedingNode = successors.get(0);
            graph.remove(closestPrecedingNode, successorOfClosestPrecedingNode);
            graph.add(closestPrecedingNode, node);
            graph.add(node, successorOfClosestPrecedingNode);
            // We just add the node to the scope if it is within the scope max X bounds
            if (dropPoint.x < scopeMaxXBound) {
                closestPrecedingNode.addToScope(node);
            }
            return;
        }

        for (int successorIndex = 0; successorIndex < successors.size(); successorIndex++) {
            // |-----------| yTopTopBound
            // |    top    |
            // |-----------| yCenterTopBound == yTopBottomBound
            // |   center  |
            // |-----------| yCenterBottomBound == yBottomTopBound
            // |  bottom   |
            // |-----------| yBottomBottomBound
            GraphNode successor = successors.get(successorIndex);

            if (isInsideTopArea(successors, successorIndex, closestPrecedingNode, dropPoint)) {
                // Adds a node at index "successorIndex", the existing nodes are shifted down.
                if (node.isSuccessorAllowed(graph, closestPrecedingNode, successorIndex)) {
                    graph.add(closestPrecedingNode, node, successorIndex);
                    closestPrecedingNode.addToScope(node);
                    FindFirstNodeOutsideScope.of(graph, closestPrecedingNode)
                            .ifPresent(firstNodeOutsideScope -> graph.add(node, firstNodeOutsideScope));
                }
                break;

            } else if (isInsideCenterArea(successor, dropPoint)) {
                // Replaces the first node at index "successorIndex".
                if (node.isSuccessorAllowed(graph, closestPrecedingNode, successorIndex)) {
                    graph.remove(closestPrecedingNode, successor);
                    graph.add(closestPrecedingNode, node, successorIndex);
                    graph.add(node, successor);
                    closestPrecedingNode.addToScope(node);
                }
                break;

            } else if (isInsideBottomArea(successors, successorIndex, closestPrecedingNode, dropPoint)) {
                if (node.isSuccessorAllowed(graph, closestPrecedingNode, successorIndex + 1)) {
                    // Adds a node next to the current index. Existing nodes at "successorIndex + 1" are shifted down.
                    graph.add(closestPrecedingNode, node, successorIndex + 1);
                    closestPrecedingNode.addToScope(node);
                    FindFirstNodeOutsideScope.of(graph, closestPrecedingNode)
                            .ifPresent(firstNodeOutsideScope -> graph.add(node, firstNodeOutsideScope));
                }
                break;
            }
        }
    }

    // |-----------| yTopScopeBound
    // |           |
    // |           |
    // |-----------| yTopTopBound
    // |    top    |
    // |-----------| yCenterTopBound == yTopBottomBound
    // |   center  |
    // |-----------| yCenterBottomBound == yBottomTopBound
    // |  bottom   |
    // |-----------| yBottomBottomBound
    private boolean isInsideTopArea(List<GraphNode> successors, int successorIndex, ScopedGraphNode closestPrecedingNode, Point dropPoint) {
        if (successorIndex == 0) { // if it is the topmost element
            // The top area is between top of scope rectangle and yTopBottomBound
            GraphNode node = successors.get(successorIndex);

            int yTopScopeBound = getScopeMinYBound(graph, closestPrecedingNode, graphics);
            int height = node.height(graphics);
            int halfHeight = Half.of(height);
            int yTopBottomBound = node.y() - (halfHeight - Math.floorDiv(height, 4));
            return dropPoint.y >= yTopScopeBound && dropPoint.y < yTopBottomBound;

        } else {
            // The top area is between the previous node yBottomTopBound and current yTopBottomBound
            GraphNode preceding = successors.get(successorIndex - 1);
            GraphNode current = successors.get(successorIndex);

            int height = preceding.height(graphics);
            int halfHeight = Half.of(height);
            int yBottomTopBound = preceding.y() + (halfHeight - Math.floorDiv(height, 4));

            height = current.height(graphics);
            halfHeight = Half.of(height);
            int yTopBottomBound = current.y() - (halfHeight - Math.floorDiv(height, 4));

            return dropPoint.y >= yBottomTopBound && dropPoint.y < yTopBottomBound;
        }
    }


    // |   center  |
    // |-----------| yCenterBottomBound == yBottomTopBound
    // |  bottom   |
    // |-----------| yBottomBottomBound
    // |           |
    // |           |
    // |-----------| yBottomScopeBound
    private boolean isInsideBottomArea(List<GraphNode> successors, int successorIndex, ScopedGraphNode closestPrecedingNode, Point dropPoint) {
        // if it is the lowermost element
        if (successorIndex == successors.size() - 1) {
            // The bottom area is between yBottomTopBound and the bottom of the scope rectangle

            GraphNode node = successors.get(successorIndex);
            int yBottomScopeBound = getScopeMaxYBound(graph, closestPrecedingNode, graphics);
            int height = node.height(graphics);
            int halfHeight = Half.of(height);
            int yBottomTopBound = node.y() + (halfHeight - Math.floorDiv(height, 4));
            return dropPoint.y <= yBottomScopeBound && dropPoint.y > yBottomTopBound;

        } else {
            // The bottom area is between the following yTopTopBound and current yTopBottomBound
            GraphNode following = successors.get(successorIndex + 1);
            GraphNode current = successors.get(successorIndex);

            int height = current.height(graphics);
            int halfHeight = Half.of(height);
            int yBottomTopBound = current.y() + (halfHeight - Math.floorDiv(height, 4));

            height = following.height(graphics);
            halfHeight = Half.of(height);
            int yTopBottomBound = following.y() - (halfHeight - Math.floorDiv(height, 4));


            return dropPoint.y > yTopBottomBound && dropPoint.y < yBottomTopBound;
        }
    }

    private boolean isInsideCenterArea(GraphNode node, Point dropPoint) {
        int height = node.height(graphics);
        int halfHeight = Half.of(height);
        int yCenterTopBound = node.y() - (halfHeight - Math.floorDiv(height, 4));
        int yCenterBottomBound = node.y() + (halfHeight - Math.floorDiv(height, 4));
        return dropPoint.y >= yCenterTopBound && dropPoint.y <= yCenterBottomBound;
    }
}
