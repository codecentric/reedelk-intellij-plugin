package com.esb.plugin.designer.graph.dnd;

import com.esb.plugin.designer.Tile;
import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.drawable.Drawable;
import com.esb.plugin.designer.graph.drawable.DrawableFactory;
import com.esb.plugin.designer.graph.drawable.ScopedDrawable;

import java.awt.*;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static com.esb.plugin.designer.graph.dnd.DndPredicates.byPrecedingNodesOnX;
import static java.util.stream.Collectors.toList;

/**
 * Adds to the graph a new node representing the Component Name to the given location.
 * The returned graph is a copy. An empty optional is returned if the node could not be
 * added.
 * <p>
 * This class find the best position where to place the node in the Graph given
 * the drop point location
 */
public class GraphNodeAdder {

    public Optional<FlowGraph> add(FlowGraph graph, Point dropPoint, String componentName) {

        Drawable genericDrawable = DrawableFactory.get(componentName);

        if (graph == null) graph = new FlowGraph();

        FlowGraph copy = graph.copy();
        if (graph.nodes().isEmpty()) {
            copy.add(null, genericDrawable);
            return Optional.of(copy);
        }

        int dropX = dropPoint.x;
        int dropY = dropPoint.y;

        // If does not exists a node before the current dropX, then we are replacing the root.
        if (GraphNodeAdderUtilities.isReplacingRoot(graph, dropX)) {
            copy.add(genericDrawable);
            copy.add(genericDrawable, graph.root());
            copy.root(genericDrawable);
            return Optional.of(copy);
        }

        // Find all preceding nodes on X axis
        List<Drawable> precedingNodes = graph
                .nodes()
                .stream()
                .filter(byPrecedingNodesOnX(graph, dropX))
                .collect(toList());

        // Amongst all preceding nodes find the closest on the Y axis
        Drawable closestPrecedingDrawable = GraphNodeAdderUtilities.findClosestOnYAxis(precedingNodes, dropY);

        if (closestPrecedingDrawable instanceof ScopedDrawable) {
            handlePrecedingMultipathDrawable(graph, genericDrawable, copy, dropY, closestPrecedingDrawable);

        } else if (closestPrecedingDrawable != null) {
            handlePrecedingDrawable(graph, genericDrawable, copy, dropX, dropY, closestPrecedingDrawable);

        } else {
            // closest preceding node is null, the drop point is too far.
            // Nothing to do. Original graph is unchanged.
        }

        return Optional.of(copy);
    }

    private void handlePrecedingDrawable(FlowGraph graph, Drawable genericDrawable, FlowGraph copy, int dropX, int dropY, Drawable closestPrecedingNode) {
        List<Drawable> successors = graph.successors(closestPrecedingNode);
        if (successors.isEmpty()) {
            // This is the last node:
            // If the closestPreceding node belongs to a scope, then:
            // If it is in the first half, then we add it to that scope,
            // otherwise we add it outside
            List<ScopedDrawable> nodeScopedDrawables = ScopeUtilities.findScopesForNode(graph, closestPrecedingNode);
            if (nodeScopedDrawables.isEmpty()) {
                copy.add(closestPrecedingNode, genericDrawable);
                ScopeUtilities.addToScopeIfNecessary(graph, closestPrecedingNode, genericDrawable);
            } else {
                // The closest preceding node belongs to a scope, we need to check if it is within the
                // scope boundaries
                if (dropX <= ScopeUtilities.getScopeXEdge(nodeScopedDrawables.get(0))) {
                    copy.add(closestPrecedingNode, genericDrawable);
                    ScopeUtilities.addToScopeIfNecessary(graph, closestPrecedingNode, genericDrawable);

                    // The reality is that here we need to find the boundaries of the scope
                    // Get scope x edge:
                } else if (dropX > ScopeUtilities.getScopeXEdge(nodeScopedDrawables.get(0))) {
                    // Add it outside the scope and connect all child nodes
                    Collection<Drawable> nodesConnectedToZeroOrOutsideScopeDrawables = ScopeUtilities.findNodesConnectedToZeroOrOutsideScopeDrawables(graph, nodeScopedDrawables.get(0));
                    for (Drawable lastNode : nodesConnectedToZeroOrOutsideScopeDrawables) {
                        copy.add(lastNode, genericDrawable);
                    }

                } else {//TODO: Should never get to this point
                    throw new RuntimeException("Should never get to this point");
                }
            }

            return;
        }

        // Successor MUST be 1, otherwise it would be a Scoped Drawable.
        Drawable successorOfClosestPrecedingNode = successors.get(0);

        if (ScopeUtilities.belongToDifferentScopes(graph, successorOfClosestPrecedingNode, closestPrecedingNode)) {

            List<ScopedDrawable> nodeScopedDrawables = ScopeUtilities.findScopesForNode(graph, closestPrecedingNode);

            if (dropX <= ScopeUtilities.getScopeXEdge(nodeScopedDrawables.get(0))) {
                copy.add(closestPrecedingNode, genericDrawable);
                copy.remove(closestPrecedingNode, successorOfClosestPrecedingNode);
                copy.add(genericDrawable, successorOfClosestPrecedingNode);
                ScopeUtilities.addToScopeIfNecessary(graph, closestPrecedingNode, genericDrawable);

                //dropX > ScopeUtilities.getScopeXEdge(nodeScopedDrawables.get(0))
            } else {
                // Add it outside the scope and connect all child nodes
                Collection<Drawable> nodesConnectedToZeroOrOutsideScopeDrawables = ScopeUtilities.findNodesConnectedToZeroOrOutsideScopeDrawables(graph, nodeScopedDrawables.get(0));
                for (Drawable lastNode : nodesConnectedToZeroOrOutsideScopeDrawables) {
                    copy.add(lastNode, genericDrawable);
                    copy.remove(lastNode, successorOfClosestPrecedingNode);
                }
                copy.add(genericDrawable, successorOfClosestPrecedingNode);
            }

        } else {
            if (GraphNodeAdderUtilities.withinYBounds(dropY, closestPrecedingNode)) {
                copy.add(closestPrecedingNode, genericDrawable);
                copy.add(genericDrawable, successorOfClosestPrecedingNode);
                copy.remove(closestPrecedingNode, successorOfClosestPrecedingNode);
                ScopeUtilities.addToScopeIfNecessary(graph, closestPrecedingNode, genericDrawable);
            }
        }
    }



    private void handlePrecedingMultipathDrawable(FlowGraph graph, Drawable genericDrawable, FlowGraph copy, int dropY, Drawable closestPrecedingNode) {
        List<Drawable> successors = graph.successors(closestPrecedingNode);
        for (int i = 0; i < successors.size(); i++) {
            Drawable successor = successors.get(i);

            // We look if it goes in between two nodes
            int yTopBound = successor.y() - Tile.HALF_HEIGHT + Math.floorDiv(Tile.HEIGHT, 4);
            int yBottomBound = successor.y() - Tile.HALF_HEIGHT - Math.floorDiv(Tile.HEIGHT, 4);
            if (dropY > yBottomBound && dropY < yTopBound) {
                copy.add(closestPrecedingNode, genericDrawable, i);
                ScopeUtilities.addToScopeIfNecessary(graph, closestPrecedingNode, genericDrawable);
                // Need to find the first common successor:
                Optional<Drawable> common = ScopeUtilities.findFirstNotInCollection(graph, ((ScopedDrawable) closestPrecedingNode).listDrawables(), closestPrecedingNode);
                if (common.isPresent()) {
                    Drawable commonSucessor = common.get();
                    copy.add(genericDrawable, commonSucessor);
                }
                return;
            }
        }

        // Case where there are no successors to choice or it does not belong before anything.
        // At to the end.

        // This is the first node to be added of the choice component
        copy.add(closestPrecedingNode, genericDrawable);
        ScopeUtilities.addToScopeIfNecessary(graph, closestPrecedingNode, genericDrawable);
        // Need to find the first common successor:
        Optional<Drawable> common = ScopeUtilities.findFirstNotInCollection(graph, ((ScopedDrawable) closestPrecedingNode).listDrawables(), closestPrecedingNode);
        if (common.isPresent()) {
            Drawable commonSucessor = common.get();
            copy.add(genericDrawable, commonSucessor);
        }

    }

}
