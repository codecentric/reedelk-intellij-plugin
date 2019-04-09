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
    private final Drawable componentToAdd;

    private final FlowGraph graph;
    private final FlowGraph modifiableGraph;

    public AddComponent(FlowGraph graph, Point dropPoint, Drawable componentToAdd) {
        this.graph = graph == null ? new FlowGraph() : graph;
        this.modifiableGraph = this.graph.copy();
        this.dropPoint = dropPoint;
        this.componentToAdd = componentToAdd;
    }


    public Optional<FlowGraph> add() {
        // First Drawable added to the canvas (it is root)
        if (graph.isEmpty()) {
            modifiableGraph.add(null, componentToAdd);
            return Optional.of(modifiableGraph);

            // Check if we are replacing the first (root) node.
        } else if (isReplacingRoot(graph, dropPoint)) {
            modifiableGraph.add(componentToAdd);
            modifiableGraph.add(componentToAdd, graph.root());
            modifiableGraph.root(componentToAdd);
            return Optional.of(modifiableGraph);

        } else {

            Optional<Drawable> optionalClosestPrecedingDrawable = findClosestPrecedingDrawable(graph, dropPoint);
            if (optionalClosestPrecedingDrawable.isPresent()) {
                Drawable closestPrecedingDrawable = optionalClosestPrecedingDrawable.get();
                if (closestPrecedingDrawable instanceof ScopedDrawable) {
                    scopedPrecedingDrawable((ScopedDrawable) closestPrecedingDrawable);
                } else {
                    precedingDrawable(closestPrecedingDrawable);
                }
                return Optional.of(modifiableGraph);
            }
        }

        // Nothing was changed (the component could not be added to the graph)
        return Optional.empty();
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
            modifiableGraph.add(closestPrecedingNode, componentToAdd);
            ScopeUtilities.addToScopeIfNecessary(graph, closestPrecedingNode, componentToAdd);

        } else {
            if (dropPoint.x <= ScopeUtilities.getScopeXEdge(nodeScopedDrawables.get(0))) {
                modifiableGraph.add(closestPrecedingNode, componentToAdd);
                ScopeUtilities.addToScopeIfNecessary(graph, closestPrecedingNode, componentToAdd);

            } else {
                ScopeUtilities
                        .findNodesConnectedToZeroOrOutsideScopeDrawables(graph, nodeScopedDrawables.get(0))
                        .forEach(lastNode -> modifiableGraph.add(lastNode, componentToAdd));
            }
        }
    }


    private void precedingDrawableWithOneSuccessor(final Drawable closestPrecedingNode, final Drawable successorOfClosestPrecedingNode) {

        if (ScopeUtilities.haveSameScope(graph, closestPrecedingNode, successorOfClosestPrecedingNode)) {
            if (withinYBounds(dropPoint.y, closestPrecedingNode)) {
                modifiableGraph.add(closestPrecedingNode, componentToAdd);
                modifiableGraph.add(componentToAdd, successorOfClosestPrecedingNode);
                modifiableGraph.remove(closestPrecedingNode, successorOfClosestPrecedingNode);
                ScopeUtilities.addToScopeIfNecessary(graph, closestPrecedingNode, componentToAdd);
            }
            return;
        }

        // They belong to different scopes

        Optional<ScopedDrawable> optionalClosestPrecedingNodeScope = ScopeUtilities.findScope(graph, closestPrecedingNode);

        if (optionalClosestPrecedingNodeScope.isPresent()) {

            // The drop point is inside the closestPrecedingNodeScope
            ScopedDrawable closestPrecedingNodeScope = optionalClosestPrecedingNodeScope.get();
            if (dropPoint.x <= ScopeUtilities.getScopeXEdge(closestPrecedingNodeScope)) {
                modifiableGraph.add(closestPrecedingNode, componentToAdd);
                modifiableGraph.add(componentToAdd, successorOfClosestPrecedingNode);
                modifiableGraph.remove(closestPrecedingNode, successorOfClosestPrecedingNode);
                closestPrecedingNodeScope.addToScope(componentToAdd);

            } else {
                // The drop point is outside the closestPrecedingNodeScope
                // Find the scope where it belongs to.
                ScopeUtilities
                        .findNodesConnectedToZeroOrOutsideScopeDrawables(graph, closestPrecedingNodeScope)
                        .forEach(lastNode -> {
                            modifiableGraph.add(lastNode, componentToAdd);
                            modifiableGraph.remove(lastNode, successorOfClosestPrecedingNode);
                        });
                modifiableGraph.add(componentToAdd, successorOfClosestPrecedingNode);
            }
        }

    }

    // It is the only type of node with potentially many successors.
    private void scopedPrecedingDrawable(ScopedDrawable precedingScopedDrawable) {
        List<Drawable> successors = graph.successors(precedingScopedDrawable);

        for (int successorIndex = 0; successorIndex < successors.size(); successorIndex++) {
            // We search the node on which the drop point lies on the Y axis.
            // If one is found, we stop.
            Drawable successor = successors.get(successorIndex);
            int yTopBound = successor.y() - Tile.HALF_HEIGHT + Math.floorDiv(Tile.HEIGHT, 4);
            int yBottomBound = successor.y() - Tile.HALF_HEIGHT - Math.floorDiv(Tile.HEIGHT, 4);

            if (dropPoint.y > yBottomBound && dropPoint.y < yTopBound) {
                modifiableGraph.add(precedingScopedDrawable, componentToAdd, successorIndex);
                ScopeUtilities.addToScopeIfNecessary(graph, precedingScopedDrawable, componentToAdd);
                connectCommonSuccessor(precedingScopedDrawable);
                return;
            }
        }

        // If we get to this point, then the node to add is in the LAST position
        // OR it is the first node to be added to the choice component.
        modifiableGraph.add(precedingScopedDrawable, componentToAdd);
        ScopeUtilities.addToScopeIfNecessary(graph, precedingScopedDrawable, componentToAdd); // add this method to the ScopedDrawable object
        connectCommonSuccessor(precedingScopedDrawable);
    }

    private void connectCommonSuccessor(ScopedDrawable closestPrecedingNode) {
        Optional<Drawable> commonSuccessor = ScopeUtilities.findFirstNodeOutsideScope(graph, closestPrecedingNode);
        if (commonSuccessor.isPresent()) {
            Drawable successor = commonSuccessor.get();
            modifiableGraph.add(componentToAdd, successor);
        }
    }
}
