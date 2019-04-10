package com.esb.plugin.designer.graph;

import com.esb.plugin.designer.Tile;
import com.esb.plugin.designer.graph.drawable.Drawable;
import com.esb.plugin.designer.graph.drawable.ScopedDrawable;

import java.awt.*;
import java.util.List;
import java.util.Optional;

import static com.esb.plugin.designer.graph.AddDrawableToGraphUtilities.*;
import static com.google.common.base.Preconditions.checkState;

/**
 * Adds to the graph a new node representing the Component Name to the given location.
 * This class find the best position where to place the node in the Graph given the drop point location.
 */
public class AddDrawableToGraph {

    private final Point dropPoint;
    private final Connector connector;
    private final FlowGraph modifiableGraph;

    public AddDrawableToGraph(FlowGraph graph, Point dropPoint, Connector connector) {
        this.dropPoint = dropPoint;
        this.modifiableGraph = graph;
        this.connector = connector;
    }

    public void add() {
        // First Drawable added to the canvas (it is root)
        if (modifiableGraph.isEmpty()) {
            connector.addPredecessor(null);

            // Check if we are replacing the first (root) node.
        } else if (isReplacingRoot(modifiableGraph, dropPoint)) {
            connector.add();
            connector.addSuccessor(modifiableGraph.root());
            connector.root();

        } else {

            Optional<Drawable> optionalClosestPrecedingDrawable = findClosestPrecedingDrawable(modifiableGraph, dropPoint);
            if (optionalClosestPrecedingDrawable.isPresent()) {
                Drawable closestPrecedingDrawable = optionalClosestPrecedingDrawable.get();
                if (closestPrecedingDrawable instanceof ScopedDrawable) {
                    scopedPrecedingDrawable((ScopedDrawable) closestPrecedingDrawable);
                } else {
                    precedingDrawable(closestPrecedingDrawable);
                }
            }
        }
    }

    private void precedingDrawable(Drawable closestPrecedingNode) {
        List<Drawable> successors = modifiableGraph.successors(closestPrecedingNode);
        if (successors.isEmpty()) {
            precedingDrawableWithoutSuccessors(closestPrecedingNode);

        } else {
            checkState(successors.size() == 1, "Successors size MUST be 1, otherwise it is a Scoped Drawable");
            Drawable successorOfClosestPrecedingNode = successors.get(0);
            precedingDrawableWithOneSuccessor(closestPrecedingNode, successorOfClosestPrecedingNode);
        }
    }

    /**
     * Handles the case where the closest preceding node is the LAST NODE.
     * If the closest preceding node belongs to a scope, then:
     * - if it is within the scope x edge, then add it to that scope.
     * - if it is NOT within the scope x edge, add it outside and connect all last nodes from the scope
     * If the closest preceding node does not belong to a scope, then:
     * - Add the node as successor.
     */
    private void precedingDrawableWithoutSuccessors(Drawable closestPrecedingNode) {
        List<ScopedDrawable> nodeScopedDrawables = ScopeUtilities.findScopesForNode(modifiableGraph, closestPrecedingNode);
        if (nodeScopedDrawables.isEmpty()) {
            connector.addPredecessor(closestPrecedingNode);
            addToScopeIfNeeded(closestPrecedingNode);

        } else {
            if (dropPoint.x <= ScopeUtilities.getScopeXEdge(nodeScopedDrawables.get(0))) {
                connector.addPredecessor(closestPrecedingNode);
                addToScopeIfNeeded(closestPrecedingNode);

            } else {
                ScopeUtilities
                        .findNodesConnectedToZeroOrOutsideScopeDrawables(modifiableGraph, nodeScopedDrawables.get(0))
                        .forEach(connector::addPredecessor);
            }
        }
    }


    private void precedingDrawableWithOneSuccessor(final Drawable closestPrecedingNode, final Drawable successorOfClosestPrecedingNode) {

        if (ScopeUtilities.haveSameScope(modifiableGraph, closestPrecedingNode, successorOfClosestPrecedingNode)) {
            if (withinYBounds(dropPoint.y, closestPrecedingNode)) {
                connector.addPredecessor(closestPrecedingNode);
                connector.addSuccessor(successorOfClosestPrecedingNode);
                modifiableGraph.remove(closestPrecedingNode, successorOfClosestPrecedingNode);
                addToScopeIfNeeded(closestPrecedingNode);
            }
            return;
        }

        // They belong to different scopes

        Optional<ScopedDrawable> optionalClosestPrecedingNodeScope = ScopeUtilities.findScope(modifiableGraph, closestPrecedingNode);

        if (optionalClosestPrecedingNodeScope.isPresent()) {

            // The drop point is inside the closestPrecedingNodeScope
            ScopedDrawable closestPrecedingNodeScope = optionalClosestPrecedingNodeScope.get();
            if (dropPoint.x <= ScopeUtilities.getScopeXEdge(closestPrecedingNodeScope)) {
                connector.addPredecessor(closestPrecedingNode);
                connector.addToScope(closestPrecedingNodeScope);
                connector.addSuccessor(successorOfClosestPrecedingNode);
                modifiableGraph.remove(closestPrecedingNode, successorOfClosestPrecedingNode);

            } else {
                // The drop point is outside the closestPrecedingNodeScope
                // Find the scope where it belongs to.
                ScopeUtilities
                        .findNodesConnectedToZeroOrOutsideScopeDrawables(modifiableGraph, closestPrecedingNodeScope)
                        .forEach(lastNode -> {
                            connector.addPredecessor(lastNode);
                            modifiableGraph.remove(lastNode, successorOfClosestPrecedingNode);
                        });
                connector.addSuccessor(successorOfClosestPrecedingNode);
            }
        }
    }

    // It is the only type of node with potentially many successors.
    private void scopedPrecedingDrawable(ScopedDrawable precedingScopedDrawable) {
        List<Drawable> successors = modifiableGraph.successors(precedingScopedDrawable);

        for (int successorIndex = 0; successorIndex < successors.size(); successorIndex++) {
            // We search the node on which the drop point lies on the Y axis.
            // If one is found, we stop.
            Drawable successor = successors.get(successorIndex);
            int yTopBound = successor.y() - Tile.HALF_HEIGHT + Math.floorDiv(Tile.HEIGHT, 4);
            int yBottomBound = successor.y() - Tile.HALF_HEIGHT - Math.floorDiv(Tile.HEIGHT, 4);

            if (dropPoint.y > yBottomBound && dropPoint.y < yTopBound) {
                connector.addPredecessor(precedingScopedDrawable, successorIndex);
                addToScopeIfNeeded(precedingScopedDrawable);
                linkCommonSuccessors(precedingScopedDrawable);
                return;
            }
        }

        // If we get to this point, then the node to add is in the LAST position
        // OR it is the first node to be added to the choice component.
        connector.addPredecessor(precedingScopedDrawable);
        addToScopeIfNeeded(precedingScopedDrawable);
        linkCommonSuccessors(precedingScopedDrawable);
    }

    private void linkCommonSuccessors(ScopedDrawable closestPrecedingNode) {
        Optional<Drawable> commonSuccessor = ScopeUtilities.findFirstNodeOutsideScope(modifiableGraph, closestPrecedingNode);
        if (commonSuccessor.isPresent()) {
            Drawable successor = commonSuccessor.get();
            connector.addSuccessor(successor);
        }
    }

    private void addToScopeIfNeeded(Drawable closestPrecedingNode) {
        ScopeUtilities.addToScopeIfNecessary(modifiableGraph, closestPrecedingNode, connector);
    }

}
