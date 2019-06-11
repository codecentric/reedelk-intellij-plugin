package com.esb.plugin.graph.action.strategy;

import com.esb.plugin.commons.Half;
import com.esb.plugin.component.type.placeholder.PlaceholderNode;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.ScopeBoundaries;
import com.esb.plugin.graph.node.ScopedGraphNode;

import java.awt.*;
import java.awt.image.ImageObserver;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static com.google.common.base.Preconditions.checkState;
import static java.util.stream.Collectors.toList;

public class StrategyBuilder {

    private FlowGraph graph;
    private Point dropPoint;
    private Graphics2D graphics;
    private ImageObserver observer;

    public StrategyBuilder graph(FlowGraph graph) {
        this.graph = graph;
        return this;
    }

    public StrategyBuilder graphics(Graphics2D graphics) {
        this.graphics = graphics;
        return this;
    }

    public StrategyBuilder dropPoint(Point dropPoint) {
        this.dropPoint = dropPoint;
        return this;
    }

    public StrategyBuilder imageObserver(ImageObserver observer) {
        this.observer = observer;
        return this;
    }

    private StrategyBuilder() {
    }

    public static StrategyBuilder create() {
        return new StrategyBuilder();
    }


    public Strategy build() {
        // First node
        Strategy strategy = new NoOpStrategy();
        if (graph.isEmpty()) {
            strategy = new AddRootStrategy(graph);

        } else if (isReplacingRoot(graph, dropPoint)) {
            strategy = new ReplaceRootStrategy(graph);

        } else if (overlapsPlaceholder(graph, dropPoint)) {
            GraphNode overlappingPlaceholder = getOverlappingPlaceholder(graph, dropPoint);
            strategy = new ReplacePlaceholderStrategy(graph, overlappingPlaceholder);

        } else {

            Optional<GraphNode> closestPrecedingNode = findClosestPrecedingNode(graph, dropPoint, graphics);

            if (closestPrecedingNode.isPresent()) {

                GraphNode precedingNode = closestPrecedingNode.get();

                if (precedingNode instanceof ScopedGraphNode) {
                    strategy = getScopedGraphNodeStrategy((ScopedGraphNode) precedingNode);

                } else if (graph.successors(precedingNode).isEmpty()) {
                    strategy = new PrecedingNodeWithoutSuccessor(graph, dropPoint, precedingNode, graphics);

                } else {
                    // Only ScopedGraphNode nodes might have multiple successors. In all other cases
                    // a node in the flow must have at most one successor.
                    checkState(graph.successors(precedingNode).size() == 1,
                            "Successors size MUST be 1, otherwise it must be a Scoped Drawable");
                    strategy = new PrecedingNodeWithOneSuccessor(graph, dropPoint, precedingNode, graphics);
                }
            }
        }
        return strategy;
    }

    private Strategy getScopedGraphNodeStrategy(ScopedGraphNode scopedGraphNode) {
        if (hasOnlyOneSuccessorOutsideScope(scopedGraphNode, graph)) {
            return new PrecedingNodeWithOneSuccessor(graph, dropPoint, scopedGraphNode, graphics);
        } else if (graph.successors(scopedGraphNode).isEmpty()) {
            return new PrecedingNodeWithoutSuccessor(graph, dropPoint, scopedGraphNode, graphics);
        } else {
            return new PrecedingScopedNode(graph, dropPoint, scopedGraphNode, graphics);
        }
    }

    private boolean hasOnlyOneSuccessorOutsideScope(ScopedGraphNode closestPrecedingNode, FlowGraph graph) {
        List<GraphNode> successors = graph.successors(closestPrecedingNode);
        return successors.size() == 1 && !closestPrecedingNode.scopeContains(successors.get(0));
    }

    private boolean overlapsPlaceholder(FlowGraph graph, Point dropPoint) {
        return graph.nodes()
                .stream()
                .anyMatch(node -> node.contains(observer, dropPoint.x, dropPoint.y) &&
                        node instanceof PlaceholderNode);
    }

    private GraphNode getOverlappingPlaceholder(FlowGraph graph, Point dropPoint) {
        return graph.nodes()
                .stream()
                .filter(node -> node.contains(observer, dropPoint.x, dropPoint.y) &&
                        node instanceof PlaceholderNode)
                .findFirst()
                .get();
    }

    /*
     * Checks if we are replacing the root (i.e there are no nodes preceding the drop point on X).
     * TODO: do a check on the Y axis as well.
     */
    private static boolean isReplacingRoot(FlowGraph graph, Point dropPoint) {
        return graph
                .nodes()
                .stream()
                .noneMatch(node -> node.x() < dropPoint.x);
    }


    private static Optional<GraphNode> findClosestPrecedingNode(FlowGraph graph, Point dropPoint, Graphics2D graphics) {
        java.util.List<GraphNode> precedingNodes = graph
                .nodes()
                .stream()
                .filter(byPrecedingNodesOnX(graph, dropPoint.x, graphics))
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

    // A value which represent the max vicinity a drop point must
    // have in order to be snapped to a predecessor.
    private static final int MAX_SNAP_VICINITY = 110;


    private static Predicate<GraphNode> byPrecedingNodesOnX(FlowGraph graph, int dropX, Graphics2D graphics) {
        return preceding -> {
            // The drop point is before/after the center of the node or the center + next node position.
            if (dropX <= preceding.x() || dropX >= preceding.x() + preceding.width(graphics) + Half.of(preceding.width(graphics))) {
                return false;
            }
            // TODO: Test this function
            if (preceding instanceof ScopedGraphNode) {
                ScopeBoundaries scopeBoundaries = ((ScopedGraphNode) preceding).getScopeBoundaries(graph, graphics);
                // We don't consider the scope node a predecessor
                // if the dropX point is after the scope boundary + MAX_SNAP_VICINITY
                if (dropX >= scopeBoundaries.getX() + scopeBoundaries.getWidth() + MAX_SNAP_VICINITY) {
                    return false;
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
}
