package com.esb.plugin.graph.action;


import com.esb.plugin.designer.Tile;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.action.strategy.AddStrategy;
import com.esb.plugin.graph.action.strategy.PrecedingDrawableWithOneSuccessor;
import com.esb.plugin.graph.action.strategy.PrecedingDrawableWithoutSuccessor;
import com.esb.plugin.graph.action.strategy.PrecedingScopedDrawable;
import com.esb.plugin.graph.connector.Connector;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.ScopedDrawable;

import java.awt.*;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static com.google.common.base.Preconditions.checkState;
import static java.util.stream.Collectors.toList;

/**
 * Adds to the graph a new node representing the Component Name to the given location.
 * This class find the best position where to place the node in the Graph given the drop point location.
 */
public class AddNodeAction {

    private final FlowGraph graph;
    private final Point dropPoint;
    private final Connector connector;
    private final Graphics2D graphics;

    public AddNodeAction(FlowGraph graph, Point dropPoint, Connector connector, Graphics2D graphics) {
        this.graph = graph;
        this.graphics = graphics;
        this.dropPoint = dropPoint;
        this.connector = connector;
    }

    public void add() {
        // First Drawable added to the canvas (it is root)
        if (graph.isEmpty()) {
            connector.addPredecessor(null);

            // Check if we are replacing the first (root) node.
        } else if (isReplacingRoot(graph, dropPoint)) {
            connector.add();
            connector.addSuccessor(graph.root());
            connector.root();

        } else {

            Optional<GraphNode> optionalPrecedingNode = findClosestPrecedingDrawable(graph, dropPoint);
            if (optionalPrecedingNode.isPresent()) {
                GraphNode closestPrecedingDrawable = optionalPrecedingNode.get();
                AddStrategy strategy;

                if (closestPrecedingDrawable instanceof ScopedDrawable) {
                    strategy = new PrecedingScopedDrawable(graph, dropPoint, connector, graphics);

                } else if (graph.successors(closestPrecedingDrawable).isEmpty()) {
                    strategy = new PrecedingDrawableWithoutSuccessor(graph, dropPoint, connector, graphics);

                } else {
                    // Only ScopedDrawable nodes might have multiple successors. In all other cases
                    // a node in the flow must have at most one successor.
                    checkState(graph.successors(closestPrecedingDrawable).size() == 1,
                            "Successors size MUST be 1, otherwise it must be a Scoped Drawable");
                    strategy = new PrecedingDrawableWithOneSuccessor(graph, dropPoint, connector, graphics);
                }
                strategy.execute(closestPrecedingDrawable);
            }
        }
    }


    private static Optional<GraphNode> findClosestPrecedingDrawable(FlowGraph graph, Point dropPoint) {
        List<GraphNode> precedingNodes = graph
                .nodes()
                .stream()
                .filter(byPrecedingNodesOnX(graph, dropPoint.x))
                .collect(toList());
        return findClosestOnYAxis(precedingNodes, dropPoint.y, dropPoint.x);
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

    private static Predicate<GraphNode> byPrecedingNodesOnX(FlowGraph graph, int dropX) {
        return preceding -> {
            // The drop point is before/after the center of the node or the center + next node position.
            if (dropX <= preceding.x() || dropX >= preceding.x() + Tile.WIDTH + Tile.HALF_WIDTH) {
                return false;
            }
            // If exists a successor of the current preceding preceding in the preceding + 1 position,
            // then we restrict the drop position so that we consider valid if and only if its x
            // coordinates are between preceding x and successor x.
            for (GraphNode successor : graph.successors(preceding)) {
                if (successor.x() == preceding.x() + Tile.WIDTH) {
                    return dropX > preceding.x() && dropX < successor.x();
                }
            }
            // The next successor is beyond the next position so we consider valid a drop point
            // between preceding x and until the end of preceding + 1 position
            return true;
        };
    }

    /*
     * Checks if we are replacing the root (i.e there are no nodes preceding the drop point on X).
     * Do a check on the Y axis as well
     */
    public static boolean isReplacingRoot(FlowGraph graph, Point dropPoint) {
        return graph
                .nodes()
                .stream()
                .noneMatch(drawable -> drawable.x() < dropPoint.x);
    }


}
