package com.esb.plugin.designer.graph.dnd;

import com.esb.plugin.designer.Tile;
import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.drawable.Drawable;
import com.esb.plugin.designer.graph.drawable.DrawableFactory;
import com.esb.plugin.designer.graph.drawable.ScopedDrawable;

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

        if (closestPrecedingNode instanceof ScopedDrawable) {
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
            // This is the last node:
            // If the closestPreceding node belongs to a scope, then:
            // If it is in the first half, then we add it to that scope,
            // otherwise we add it outside
            List<ScopedDrawable> nodeScopedDrawables = findScopesForNode(graph, closestPrecedingNode);
            if (nodeScopedDrawables.isEmpty()) {
                copy.add(closestPrecedingNode, genericDrawable);
                addToScopeIfNecessary(graph, closestPrecedingNode, genericDrawable);
            } else {
                // The closest preceding node belongs to a scope, we need to check if it is within the
                // scope boundaries
                if (dropX <= getScopeXEdge(nodeScopedDrawables.get(0))) {
                    copy.add(closestPrecedingNode, genericDrawable);
                    addToScopeIfNecessary(graph, closestPrecedingNode, genericDrawable);

                    // The reality is that here we need to find the boundaries of the scope
                    // Get scope x edge:
                } else if (dropX > getScopeXEdge(nodeScopedDrawables.get(0))) {
                    // Add it outside the scope and connect all child nodes
                    Collection<Drawable> nodesConnectedToZeroOrOutsideScopeDrawables = findNodesConnectedToZeroOrOutsideScopeDrawables(graph, nodeScopedDrawables.get(0));
                    for (Drawable lastNode : nodesConnectedToZeroOrOutsideScopeDrawables) {
                        copy.add(lastNode, genericDrawable);
                    }

                } else {//TODO: Should never get to this point
                    throw new RuntimeException("Should never get to this point");
                }
            }

            return;
        }

        // Successor MUST be 1, otherwise it would be a multipath drawable.
        Drawable successorOfClosestPrecedingNode = successors.get(0);
        // If closest preceding node belongs to a scope and successor to another one,
        // need to detect where it belongs.
        if (belongToDifferentScopes(graph, successorOfClosestPrecedingNode, closestPrecedingNode)) {
            // Do Stuff
            List<ScopedDrawable> nodeScopedDrawables = findScopesForNode(graph, closestPrecedingNode);

            if (dropX <= getScopeXEdge(nodeScopedDrawables.get(0))) {
                copy.add(closestPrecedingNode, genericDrawable);
                copy.remove(closestPrecedingNode, successorOfClosestPrecedingNode);
                copy.add(genericDrawable, successorOfClosestPrecedingNode);
                addToScopeIfNecessary(graph, closestPrecedingNode, genericDrawable);
            } else if (dropX > getScopeXEdge(nodeScopedDrawables.get(0))) {
                // Add it outside the scope and connect all child nodes
                Collection<Drawable> nodesConnectedToZeroOrOutsideScopeDrawables = findNodesConnectedToZeroOrOutsideScopeDrawables(graph, nodeScopedDrawables.get(0));
                for (Drawable lastNode : nodesConnectedToZeroOrOutsideScopeDrawables) {
                    copy.add(lastNode, genericDrawable);
                    copy.remove(lastNode, successorOfClosestPrecedingNode);
                }
                copy.add(genericDrawable, successorOfClosestPrecedingNode);
            } else {//TODO: Should never get to this point
                throw new RuntimeException("Should never get to this point");
            }
        } else {
            if (withinYBounds(dropY, closestPrecedingNode)) {
                copy.add(closestPrecedingNode, genericDrawable);
                copy.add(genericDrawable, successorOfClosestPrecedingNode);
                copy.remove(closestPrecedingNode, successorOfClosestPrecedingNode);
                addToScopeIfNecessary(graph, closestPrecedingNode, genericDrawable);
            }
        }
    }

    private boolean belongToDifferentScopes(FlowGraph graph, Drawable drawable1, Drawable drawable2) {
        List<ScopedDrawable> scopesForNode1 = findScopesForNode(graph, drawable1);
        List<ScopedDrawable> scopesForNode2 = findScopesForNode(graph, drawable2);
        return !scopesForNode1.containsAll(scopesForNode2);
    }

    private int getScopeXEdge(ScopedDrawable scopedDrawable) {
        return scopedDrawable.listDrawables().stream().mapToInt(Drawable::x).max().getAsInt() + Tile.HALF_WIDTH;
    }

    /*
     * Checks if we are replacing the root (i.e there are no nodes preceding the drop point on X).
     * Do a check on the Y axis as well
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
                Optional<Drawable> common = findFirstNotInCollection(graph, ((ScopedDrawable) closestPrecedingNode).listDrawables(), closestPrecedingNode);
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
        Optional<Drawable> common = findFirstNotInCollection(graph, ((ScopedDrawable) closestPrecedingNode).listDrawables(), closestPrecedingNode);
        if (common.isPresent()) {
            Drawable commonSucessor = common.get();
            copy.add(genericDrawable, commonSucessor);
        }

    }

    private void addToScopeIfNecessary(FlowGraph graph, Drawable closestPrecedingNode, Drawable genericDrawable) {
        if (closestPrecedingNode instanceof ScopedDrawable) {
            ScopedDrawable scopedDrawable = (ScopedDrawable) closestPrecedingNode;
            scopedDrawable.add(genericDrawable);
        }
        List<ScopedDrawable> scopedDrawableObjects = findScopesForNode(graph, closestPrecedingNode);
        scopedDrawableObjects.forEach(scopedDrawable -> scopedDrawable.add(genericDrawable));
    }

    // A node might belong to multiple scopes....
    private List<ScopedDrawable> findScopesForNode(FlowGraph graph, Drawable targetDrawable) {
        List<ScopedDrawable> scopedDrawables = new ArrayList<>();
        Collection<Drawable> nodes = graph.nodes();
        for (Drawable node : nodes) {
            if (node instanceof ScopedDrawable) {
                if (((ScopedDrawable) node).listDrawables().contains(targetDrawable)) {
                    // But this one might be part of another scope as well...
                    scopedDrawables.add((ScopedDrawable) node);
                    List<Drawable> predecessors = graph.predecessors(node);
                    if (!predecessors.isEmpty()) {
                        for (Drawable predecessor : predecessors) {
                            scopedDrawables.addAll(findScopesForNode(graph, predecessor));
                        }

                    }
                }
            }
        }
        return scopedDrawables;
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

    private Collection<Drawable> findNodesConnectedToZeroOrOutsideScopeDrawables(FlowGraph graph, ScopedDrawable scope) {
        Collection<Drawable> drawablesInTheScope = scope.listDrawables();
        return drawablesInTheScope.stream().filter(drawable -> {
            List<Drawable> successors = graph.successors(drawable);
            if (successors.isEmpty()) return true;
            return !drawablesInTheScope.containsAll(successors);
        }).collect(toList());
    }

}
