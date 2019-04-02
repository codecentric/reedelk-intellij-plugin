package com.esb.plugin.designer.graph.dnd;

import com.esb.plugin.designer.Tile;
import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.drawable.Drawable;
import com.esb.plugin.designer.graph.drawable.DrawableFactory;
import com.esb.plugin.designer.graph.drawable.MultipathDrawable;

import java.awt.*;
import java.util.ArrayList;
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
        if (isReplacingRoot(graph, dropX)) {
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
        Drawable closestPrecedingNode = findClosestOnYAxis(precedingNodes, dropY);

        if (closestPrecedingNode instanceof MultipathDrawable) {
            handlePrecedingMultipathDrawable(graph, genericDrawable, copy, dropY, closestPrecedingNode);

        } else if (closestPrecedingNode != null) {
            handlePrecedingDrawable(graph, genericDrawable, copy, dropX, dropY, closestPrecedingNode);

        } else {
            // closest preceding node is null, the drop point is too far.
            // Nothing to do. Original graph is unchanged.
        }

        return Optional.of(copy);
    }

    private void handlePrecedingDrawable(FlowGraph graph, Drawable genericDrawable, FlowGraph copy, int dropX, int dropY, Drawable closestPrecedingNode) {
        List<Drawable> successors = graph.successors(closestPrecedingNode);
        if (successors.isEmpty()) {
            // This is the last node
            copy.add(closestPrecedingNode, genericDrawable);
            addToScopeIfNecessary(graph, closestPrecedingNode, genericDrawable);
            return;
        }

        Drawable successor = successors.get(0);

        List<Drawable> predecessors = graph.predecessors(successor);
        // one-to-one drawable connection N1 -> N2 -> N3
        if (predecessors.size() == 1) {

            if (withinYBounds(dropY, closestPrecedingNode)) {
                copy.add(closestPrecedingNode, genericDrawable);
                copy.add(genericDrawable, successor);
                copy.remove(closestPrecedingNode, successor);
                addToScopeIfNecessary(graph, closestPrecedingNode, genericDrawable);
            }

        } else if (predecessors.size() > 1) {
            // If successor of closesPrecedingNode has n > 1 predecessors:
            // many-to-one drawable connection
            // N1   N2
            //  \  /
            //   N3
            //  1. If it is first before successor minus half tile width,
            //  then attach it to closest preceding node.
            if (dropX < successor.x() - Tile.HALF_WIDTH) {

                // Check that it is aligned on Y axis on the preceding node before link it.
                if (withinYBounds(dropY, closestPrecedingNode)) {
                    copy.add(closestPrecedingNode, genericDrawable);
                    copy.add(genericDrawable, successor);
                    copy.remove(closestPrecedingNode, successor);
                    addToScopeIfNecessary(graph, closestPrecedingNode, genericDrawable);
                }

            } else {

                // 2. If dropX is between successor mid point minus half tile width, then
                // the node is going do replace the join.
                if (withinYBounds(dropY, successor)) {
                    for (Drawable predecessor : predecessors) {
                        copy.add(predecessor, genericDrawable);
                        copy.remove(predecessor, successor);
                    }
                    copy.add(genericDrawable, successor);
                }
            }
        } else {
            throw new IllegalStateException("Predecessors must not be 0 or less than 0");
        }
    }

    /*
     * Checks if we are replacing the root (i.e there are no nodes preceding the drop point on X).
     */
    private boolean isReplacingRoot(FlowGraph graph, int dropX) {
        return graph
                .nodes()
                .stream()
                .noneMatch(drawable -> drawable.x() < dropX);
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
                addToScopeIfNecessary(graph, closestPrecedingNode, genericDrawable);
                // Need to find the first common successor:
                Optional<Drawable> common = findFirstNotInCollection(graph, ((MultipathDrawable) closestPrecedingNode).getScope(), closestPrecedingNode);
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
        addToScopeIfNecessary(graph, closestPrecedingNode, genericDrawable);
        // Need to find the first common successor:
        Optional<Drawable> common = findFirstNotInCollection(graph, ((MultipathDrawable) closestPrecedingNode).getScope(), closestPrecedingNode);
        if (common.isPresent()) {
            Drawable commonSucessor = common.get();
            copy.add(genericDrawable, commonSucessor);
        }

    }

    private void addToScopeIfNecessary(FlowGraph graph, Drawable closestPrecedingNode, Drawable genericDrawable) {
        if (closestPrecedingNode instanceof MultipathDrawable) {
            MultipathDrawable multipathDrawable = (MultipathDrawable) closestPrecedingNode;
            multipathDrawable.addToScope(genericDrawable);
        }
        List<MultipathDrawable> scopeObjects = getScopeObjects(graph, closestPrecedingNode);
        scopeObjects.forEach(multipathDrawable -> multipathDrawable.addToScope(genericDrawable));
    }

    // A node might belong to multiple scopes....
    private List<MultipathDrawable> getScopeObjects(FlowGraph graph, Drawable closestPrecedingNode) {
        List<MultipathDrawable> scopes = new ArrayList<>();
        Collection<Drawable> nodes = graph.nodes();
        for (Drawable node : nodes) {
            if (node instanceof MultipathDrawable) {
                if (((MultipathDrawable) node).getScope().contains(closestPrecedingNode)) {
                    // But this one might be part of another scope as well...
                    scopes.add((MultipathDrawable) node);
                    List<Drawable> predecessors = graph.predecessors(node);
                    if (!predecessors.isEmpty()) {
                        for (Drawable predecessor : predecessors) {
                            scopes.addAll(getScopeObjects(graph, predecessor));
                        }

                    }
                }
            }
        }
        return scopes;
    }

    // Basically we need to find the first drawable outside the scope.
    // We need to find the first one outside the current scope
    private Optional<Drawable> findFirstNotInCollection(FlowGraph graph, Collection<Drawable> collection, Drawable root) {
        for (Drawable successor : graph.successors(root)) {
            if (!collection.contains(successor)) {
                return Optional.of(successor);
            }
            Optional<Drawable> found = findFirstNotInCollection(graph, collection, successor);
            if (found.isPresent()) return found;
        }
        return Optional.empty();
    }

    private Drawable findClosestOnYAxis(List<Drawable> precedingNodes, int dropY) {
        int min = Integer.MAX_VALUE;
        Drawable closestPrecedingNode = null;
        for (Drawable precedingNode : precedingNodes) {
            int delta = Math.abs(precedingNode.y() - dropY);
            if (delta < min) {
                closestPrecedingNode = precedingNode;
                min = delta;
            }
        }
        return closestPrecedingNode;
    }

    private boolean withinYBounds(int dropY, Drawable node) {
        return dropY > node.y() - Tile.HALF_HEIGHT &&
                dropY < node.y() + Tile.HALF_HEIGHT;
    }
}
