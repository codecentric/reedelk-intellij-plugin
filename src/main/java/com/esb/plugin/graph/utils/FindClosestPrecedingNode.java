package com.esb.plugin.graph.utils;

import com.esb.plugin.commons.Half;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.ScopeBoundaries;
import com.esb.plugin.graph.node.ScopedGraphNode;

import java.awt.*;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toList;

public class FindClosestPrecedingNode {

    // A value which represent the max vicinity a drop point must
    // have in order to be snapped to a predecessor.
    private static final int MAX_SNAP_VICINITY = 110;


    private FindClosestPrecedingNode() {
    }

    public static Optional<GraphNode> of(FlowGraph graph, Point dropPoint, Graphics2D graphics) {
        List<GraphNode> precedingNodes = graph
                .nodes()
                .stream()
                .filter(byPrecedingNodesOnX(graph, dropPoint.x, graphics))
                .collect(toList());
        return findClosestOnYAxis(precedingNodes, dropPoint.y, dropPoint.x);
    }

    private static Predicate<GraphNode> byPrecedingNodesOnX(FlowGraph graph, int dropX, Graphics2D graphics) {
        return preceding -> {

            // The drop point is before/after the center of the node or the center + next node position.
            if (dropX <= preceding.x() || dropX >= preceding.x() + preceding.width(graphics) + Half.of(preceding.width(graphics))) {
                return false;
            }

            if (preceding instanceof ScopedGraphNode) {
                // Boundaries + Max vicinity if it is empty.
                // Otherwise we must check that it is the closes
                // amongst all the nodes in the scope on the X axis.
                ScopedGraphNode precedingScopedGraphNode = (ScopedGraphNode) preceding;
                ScopeBoundaries scopeBoundaries = precedingScopedGraphNode.getScopeBoundaries(graph, graphics);

                Collection<GraphNode> scope = precedingScopedGraphNode.getScope();

                // If a preceding node is a ScopedGraphNode, then we have two cases:
                if (scope.isEmpty()) {
                    // If the scope does not contain any other node (IS EMPTY),
                    // then we consider it a potential preceding node if and only if the drop
                    // point is within the scope boundaries + some SNAP_VICINITY.
                    if (dropX >= scopeBoundaries.getX() + scopeBoundaries.getWidth() + MAX_SNAP_VICINITY) {
                        return false;
                    }
                } else {
                    // It the scope is NOT EMPTY, then the Scoped Graph Node is preceding
                    // if and only if it DOES NOT exist in the scope another node with
                    // x coordinates before the dropX point. Meaning that amongst all the
                    // nodes in the scope, the Scoped Node is the only one preceding the
                    // drop point.
                    for (GraphNode node : scope) {
                        // Exists a node in the scope before the dropX point?
                        if (dropX > node.x()) return false;
                    }
                }
            }

            // If exists a successor of the current preceding node in the preceding + 1 position,
            // then we restrict the drop position so that we consider valid if and only if its x
            // coordinates are between preceding x and successor x.
            for (GraphNode successor : graph.successors(preceding)) {
                if (successor.x() == preceding.x() + preceding.width(graphics)) {
                    return dropX > preceding.x() && dropX < successor.x();
                }
            }

            // The next successor is beyond the next position so we consider valid a drop point
            // between preceding x and until the end of preceding + 1 position
            return true;
        };
    }

    // If there are two on the same Y we pick the closest on X
    private static Optional<GraphNode> findClosestOnYAxis(List<GraphNode> precedingNodes, int dropY, int dropX) {
        int minY = Integer.MAX_VALUE;
        int minX = Integer.MAX_VALUE;
        GraphNode closestPrecedingNode = null;
        for (GraphNode precedingNode : precedingNodes) {
            int delta = Math.abs(precedingNode.y() - dropY);
            if (delta < minY) {
                closestPrecedingNode = precedingNode;
                minY = delta;
                minX = precedingNode.x();
            } else if (delta == minY) {
                if (Math.abs(dropX - precedingNode.x()) < Math.abs(minX - dropX)) {
                    closestPrecedingNode = precedingNode;
                    minX = precedingNode.x();
                }
            }
        }
        return Optional.ofNullable(closestPrecedingNode);
    }
}
