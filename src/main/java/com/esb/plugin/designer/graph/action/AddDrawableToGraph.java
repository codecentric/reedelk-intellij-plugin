package com.esb.plugin.designer.graph.action;


import com.esb.plugin.designer.Tile;
import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.action.strategy.AddStrategy;
import com.esb.plugin.designer.graph.action.strategy.PrecedingDrawableWithOneSuccessor;
import com.esb.plugin.designer.graph.action.strategy.PrecedingDrawableWithoutSuccessor;
import com.esb.plugin.designer.graph.action.strategy.PrecedingScopedDrawable;
import com.esb.plugin.designer.graph.connector.Connector;
import com.esb.plugin.designer.graph.drawable.Drawable;
import com.esb.plugin.designer.graph.drawable.ScopedDrawable;

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
public class AddDrawableToGraph {

    private final FlowGraph graph;
    private final Point dropPoint;
    private final Connector connector;

    public AddDrawableToGraph(FlowGraph graph, Point dropPoint, Connector connector) {
        this.dropPoint = dropPoint;
        this.graph = graph;
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

            findClosestPrecedingDrawable(graph, dropPoint)
                    .ifPresent(closestPrecedingDrawable -> {
                        AddStrategy strategy;
                        if (closestPrecedingDrawable instanceof ScopedDrawable) {
                            strategy = new PrecedingScopedDrawable(graph, dropPoint, connector);
                        } else if (graph.successors(closestPrecedingDrawable).isEmpty()) {
                            strategy = new PrecedingDrawableWithoutSuccessor(graph, dropPoint, connector);
                        } else {
                            // Only ScopedDrawable nodes might have multiple successors. In all other cases
                            // a node in the flow must have at most one successor.
                            checkState(graph.successors(closestPrecedingDrawable).size() == 1,
                                    "Successors size MUST be 1, otherwise it must be a Scoped Drawable");
                            strategy = new PrecedingDrawableWithOneSuccessor(graph, dropPoint, connector);
                        }
                        strategy.execute(closestPrecedingDrawable);

                    });
        }
    }


    private static Optional<Drawable> findClosestPrecedingDrawable(FlowGraph graph, Point dropPoint) {
        List<Drawable> precedingNodes = graph
                .nodes()
                .stream()
                .filter(byPrecedingNodesOnX(graph, dropPoint.x))
                .collect(toList());
        return findClosestOnYAxis(precedingNodes, dropPoint.y, dropPoint.x);
    }

    // If there are two on the same Y we pick the closest on X
    private static Optional<Drawable> findClosestOnYAxis(List<Drawable> precedingNodes, int dropY, int dropX) {
        int minY = Integer.MAX_VALUE;
        int minX = Integer.MAX_VALUE;
        Drawable closestPrecedingNode = null;
        for (Drawable precedingNode : precedingNodes) {
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

    private static Predicate<Drawable> byPrecedingNodesOnX(FlowGraph graph, int dropX) {
        return preceding -> {
            // The drop point is before/after the center of the node or the center + next node position.
            if (dropX <= preceding.x() || dropX >= preceding.x() + Tile.WIDTH + Tile.HALF_WIDTH) {
                return false;
            }
            // If exists a successor of the current preceding preceding in the preceding + 1 position,
            // then we restrict the drop position so that we consider valid if and only if its x
            // coordinates are between preceding x and successor x.
            for (Drawable successor : graph.successors(preceding)) {
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
