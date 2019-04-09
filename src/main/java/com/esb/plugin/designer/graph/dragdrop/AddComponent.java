package com.esb.plugin.designer.graph.dragdrop;

import com.esb.plugin.designer.Tile;
import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.drawable.Drawable;
import com.esb.plugin.designer.graph.drawable.ScopedDrawable;

import java.awt.*;
import java.util.List;
import java.util.Optional;

import static com.esb.plugin.designer.graph.dragdrop.GraphNodeAdderUtilities.*;
import static com.google.common.base.Preconditions.checkState;

/**
 * Adds to the graph a new node representing the Component Name to the given location.
 * The returned graph is a copy. An empty optional is returned if the node could not be
 * added.
 * <p>
 * This class find the best position where to place the node in the Graph given
 * the drop point location
 */
public class AddComponent {

    private final Point dropPoint;
    private final Drawable nodeToAdd;

    private final FlowGraph graph;
    private final FlowGraph modifiableGraph;

    public AddComponent(FlowGraph graph, Point dropPoint, Drawable componentToAdd) {
        this.graph = graph == null ? new FlowGraph() : graph;
        this.modifiableGraph = this.graph.copy();
        this.dropPoint = dropPoint;
        this.nodeToAdd = componentToAdd;
    }


    public Optional<FlowGraph> add() {
        // First Drawable added to the canvas (it is root)
        if (graph.isEmpty()) {
            modifiableGraph.add(null, nodeToAdd);

            // Check if we are replacing the first (root) node.
        } else if (isReplacingRoot(graph, dropPoint)) {
            modifiableGraph.add(nodeToAdd);
            modifiableGraph.add(nodeToAdd, graph.root());
            modifiableGraph.root(nodeToAdd);

        } else {
            findClosestPrecedingDrawable(graph, dropPoint)
                    .ifPresent(precedingDrawable -> {
                        if (precedingDrawable instanceof ScopedDrawable) {
                            scopedPrecedingDrawable((ScopedDrawable) precedingDrawable);
                        } else {
                            precedingDrawable(precedingDrawable);
                        }
                    });
        }

        return Optional.of(modifiableGraph);
    }

    private void precedingDrawable(Drawable closestPrecedingNode) {
        List<Drawable> successors = graph.successors(closestPrecedingNode);
        if (successors.isEmpty()) {
            precedingDrawableWithoutSuccessors(closestPrecedingNode);

        } else {
            checkState(successors.size() == 1,
                    "Successors size MUST be 1, otherwise it is a Scoped Drawable");
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
        List<ScopedDrawable> nodeScopedDrawables = ScopeUtilities.findScopesForNode(graph, closestPrecedingNode);
        if (nodeScopedDrawables.isEmpty()) {
            modifiableGraph.add(closestPrecedingNode, nodeToAdd);
            ScopeUtilities.addToScopeIfNecessary(graph, closestPrecedingNode, nodeToAdd);

        } else {
            if (dropPoint.x <= ScopeUtilities.getScopeXEdge(nodeScopedDrawables.get(0))) {
                modifiableGraph.add(closestPrecedingNode, nodeToAdd);
                ScopeUtilities.addToScopeIfNecessary(graph, closestPrecedingNode, nodeToAdd);

            } else {
                ScopeUtilities
                        .findNodesConnectedToZeroOrOutsideScopeDrawables(graph, nodeScopedDrawables.get(0))
                        .forEach(lastNode -> modifiableGraph.add(lastNode, nodeToAdd));
            }
        }
    }


    private void precedingDrawableWithOneSuccessor(final Drawable closestPrecedingNode, final Drawable successorOfClosestPrecedingNode) {

        if (ScopeUtilities.haveSameScope(graph, closestPrecedingNode, successorOfClosestPrecedingNode)) {
            if (withinYBounds(dropPoint.y, closestPrecedingNode)) {
                modifiableGraph.add(closestPrecedingNode, nodeToAdd);
                modifiableGraph.add(nodeToAdd, successorOfClosestPrecedingNode);
                modifiableGraph.remove(closestPrecedingNode, successorOfClosestPrecedingNode);
                ScopeUtilities.addToScopeIfNecessary(graph, closestPrecedingNode, nodeToAdd);
            }
            return;
        }

        // They belong to different scopes

        Optional<ScopedDrawable> optionalClosestPrecedingNodeScope = ScopeUtilities.findScope(graph, closestPrecedingNode);

        if (optionalClosestPrecedingNodeScope.isPresent()) {

            // The drop point is inside the closestPrecedingNodeScope
            ScopedDrawable closestPrecedingNodeScope = optionalClosestPrecedingNodeScope.get();
            if (dropPoint.x <= ScopeUtilities.getScopeXEdge(closestPrecedingNodeScope)) {
                modifiableGraph.add(closestPrecedingNode, nodeToAdd);
                modifiableGraph.add(nodeToAdd, successorOfClosestPrecedingNode);
                modifiableGraph.remove(closestPrecedingNode, successorOfClosestPrecedingNode);
                closestPrecedingNodeScope.addToScope(nodeToAdd);

            } else {
                // The drop point is outside the closestPrecedingNodeScope
                // Find the scope where it belongs to.
                ScopeUtilities
                        .findNodesConnectedToZeroOrOutsideScopeDrawables(graph, closestPrecedingNodeScope)
                        .forEach(lastNode -> {
                            modifiableGraph.add(lastNode, nodeToAdd);
                            modifiableGraph.remove(lastNode, successorOfClosestPrecedingNode);
                        });
                modifiableGraph.add(nodeToAdd, successorOfClosestPrecedingNode);
            }
        }

    }

    private void scopedPrecedingDrawable(ScopedDrawable precedingScopedDrawable) {
        List<Drawable> successors = graph.successors(precedingScopedDrawable);

        for (int successorIndex = 0; successorIndex < successors.size(); successorIndex++) {
            // We search the node on which the drop point lies on the Y axis.
            // If one is found, we stop.
            Drawable successor = successors.get(successorIndex);
            int yTopBound = successor.y() - Tile.HALF_HEIGHT + Math.floorDiv(Tile.HEIGHT, 4);
            int yBottomBound = successor.y() - Tile.HALF_HEIGHT - Math.floorDiv(Tile.HEIGHT, 4);

            if (dropPoint.y > yBottomBound && dropPoint.y < yTopBound) {
                modifiableGraph.add(precedingScopedDrawable, nodeToAdd, successorIndex);
                ScopeUtilities.addToScopeIfNecessary(graph, precedingScopedDrawable, nodeToAdd);
                connectCommonSuccessor(precedingScopedDrawable);
                return;
            }
        }
        // If we get to this point, then the node to add is in the LAST position
        // OR it is the first node to be added to the choice component.
        modifiableGraph.add(precedingScopedDrawable, nodeToAdd);
        ScopeUtilities.addToScopeIfNecessary(graph, precedingScopedDrawable, nodeToAdd); // add this method to the ScopedDrawable object
        connectCommonSuccessor(precedingScopedDrawable);
    }

    private void connectCommonSuccessor(ScopedDrawable closestPrecedingNode) {
        Optional<Drawable> commonSuccessor = ScopeUtilities.findFirstNodeOutsideScope(graph, closestPrecedingNode);
        if (commonSuccessor.isPresent()) {
            Drawable successor = commonSuccessor.get();
            modifiableGraph.add(nodeToAdd, successor);
        }
    }
}
