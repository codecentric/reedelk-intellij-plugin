package de.codecentric.reedelk.plugin.graph.utils;

import de.codecentric.reedelk.plugin.graph.FlowGraph;
import de.codecentric.reedelk.plugin.graph.node.GraphNode;
import de.codecentric.reedelk.plugin.graph.node.ScopeBoundaries;
import de.codecentric.reedelk.plugin.graph.node.ScopedGraphNode;
import de.codecentric.reedelk.plugin.commons.Half;
import de.codecentric.reedelk.plugin.commons.IsScopedGraphNode;
import de.codecentric.reedelk.plugin.commons.LiesBetweenTopAndBottom;

import java.awt.*;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Stack;
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
        return findClosestOnYAxis(precedingNodes, dropPoint.y, dropPoint.x, graph, graphics);
    }

    private static Predicate<GraphNode> byPrecedingNodesOnX(FlowGraph graph, int dropX, Graphics2D graphics) {
        return preceding -> {

            if (graph.successors(preceding).isEmpty()) {
                // If it is the last one it is good enough that it is beyond it.
                return dropX > preceding.x();
            }

            // The drop point is before the center of the preceding node or
            // after the center of the next node which is (Half.of(preceding.width(graphics)) * 2).
            if (dropX <= preceding.x() || dropX >= preceding.x() + Half.of(preceding.width(graphics)) * 2) {
                return false;
            }

            if (IsScopedGraphNode.of(preceding)) {
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

    private static Optional<GraphNode> findClosestOnYAxis(List<GraphNode> precedingNodes, int dropY, int dropX, FlowGraph graph, Graphics2D graphics) {

        for (GraphNode precedingNode : precedingNodes) {
            // If the preceding node is a ScopedGraphNode, then we consider it
            // the closest one on Y axis if and only if the drop point belongs to its scope.
            // Meaning that the drop point is within the boundaries of the node's scope.
            // If the drop point does not belong to the node's scope, then it is not
            // eligible to be considered the closest preceding node.
            // This happens when you have two or more preceding nodes, at least one
            // of which is a scoped node.
            if (IsScopedGraphNode.of(precedingNode)) {
                ScopedGraphNode scopedPrecedingNode = (ScopedGraphNode) precedingNode;
                ScopeBoundaries scopeBoundaries = scopedPrecedingNode.getScopeBoundaries(graph, graphics);
                if (dropY >= scopeBoundaries.getY() && dropY <= scopeBoundaries.getY() + scopeBoundaries.getHeight()) {
                    return Optional.of(precedingNode);
                }

            } else {
                // If the drop point Y lies between the top and bottom edges of
                // the current node, then we stop and immediately return this node.
                if (LiesBetweenTopAndBottom.of(precedingNode, dropY, graphics)) {
                    return Optional.of(precedingNode);
                }
            }
        }

        for (GraphNode precedingNode : precedingNodes) {

            // If the preceding node belongs to a scope and the drop point lies in
            // the same exact scope, then the not satisfied condition (liesBetweenTopAndBottomOf)
            // above apply and we don't keep proceeding.
            Optional<ScopedGraphNode> scopeOfPrecedingNode = FindScope.of(graph, precedingNode);
            if (scopeOfPrecedingNode.isPresent()) {
                ScopeBoundaries boundaries = scopeOfPrecedingNode.get().getScopeBoundaries(graph, graphics);
                if (dropX <= boundaries.getX() + boundaries.getWidth()) {
                    continue;
                }
            }

            // If we dropped the node right outside the scope and the preceding node is the last
            // node of the scope it belongs to, then we check that the drop point is between the
            // top and bottom boundaries of the outer scope.
            Stack<ScopedGraphNode> scopes = FindScopes.of(graph, precedingNode);
            while (!scopes.empty()) {
                ScopedGraphNode lastScopeBeforeDropPoint = scopes.pop();
                ScopeBoundaries boundaries = lastScopeBeforeDropPoint.getScopeBoundaries(graph, graphics);
                if (dropX >= boundaries.getX() + boundaries.getWidth() &&
                        dropY > boundaries.getY() && dropY < boundaries.getY() + boundaries.getHeight()) {
                    return Optional.of(precedingNode);
                }
            }
        }
        return Optional.empty();
    }
}
