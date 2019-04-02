package com.esb.plugin.designer.graph;

import com.esb.plugin.designer.Tile;
import com.esb.plugin.designer.editor.component.Component;
import com.esb.plugin.designer.graph.drawable.Drawable;
import com.esb.plugin.designer.graph.drawable.GenericComponentDrawable;
import com.esb.plugin.designer.graph.drawable.MultipathDrawable;

import java.awt.*;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static com.esb.internal.commons.Preconditions.checkState;
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


    private static Predicate<Drawable> byPrecedingNodes(FlowGraph graph, int dropX) {
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

    public Optional<FlowGraph> add(FlowGraph graph, Point dropPoint, String componentName) {

        Component component = new Component(componentName);
        component.setDescription("A description");

        int dropX = dropPoint.x;
        int dropY = dropPoint.y;

        // TODO: Handle adding first component (root)
        // TODO: Handle adding last component
        FlowGraph copy = graph.copy();

        // Find all preceding nodes on X axis
        List<Drawable> precedingNodes = graph
                .nodes()
                .stream()
                .filter(byPrecedingNodes(graph, dropX))
                .collect(toList());


        // Amongst all preceding nodes find the closest on the Y axis
        Drawable closestPrecedingNode = findClosestOnYAxis(precedingNodes, dropY);
        checkState(closestPrecedingNode != null, "Closest node could not be null");

        if (closestPrecedingNode instanceof MultipathDrawable) {

            // Need to handle the following cases:
            // 1. Add new layer in between
            // Need to find common successor
            List<Drawable> successors = graph.successors(closestPrecedingNode);
            // Need to find between where dropY lies
            // Find the bottom one

            /**
             for (int i = 0; i < successors.size(); i++) {
             Drawable successor = successors.get(i);

             // We look if it goes in between two nodes
             int yTopBound = successor.y() + Tile.HALF_HEIGHT + Math.floorDiv(Tile.HEIGHT, 4);
             int yBottomBound = successor.y() + Tile.HALF_HEIGHT - Math.floorDiv(Tile.HEIGHT, 4);
             if (dropY > yBottomBound && dropY < yTopBound) {
             System.out.println("Yeah");
             graph.breadthFirstTraversal();
             if (optional.isPresent()) {
             Drawable commonSucessor = optional.get();
             GenericComponentDrawable genericDrawable = new GenericComponentDrawable(component);
             graph.add(closestPrecedingNode, genericDrawable);
             graph.add(genericDrawable, commonSucessor);
             } else {
             throw new RuntimeException("Could not find common successor!");
             }

             }
             }*/


        } else {
            List<Drawable> successors = graph.successors(closestPrecedingNode);
            checkState(successors.size() == 1, "Successor must be one");

            Drawable successor = successors.get(0);

            List<Drawable> predecessors = graph.predecessors(successor);
            // one-to-one drawable connection N1 -> N2 -> N3
            if (predecessors.size() == 1) {

                if (withinYBounds(dropY, closestPrecedingNode)) {
                    GenericComponentDrawable genericDrawable = new GenericComponentDrawable(component);
                    copy.add(closestPrecedingNode, genericDrawable);
                    copy.add(genericDrawable, successor);
                    copy.remove(closestPrecedingNode, successor);
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
                        GenericComponentDrawable genericDrawable = new GenericComponentDrawable(component);
                        copy.add(closestPrecedingNode, genericDrawable);
                        copy.add(genericDrawable, successor);
                        copy.remove(closestPrecedingNode, successor);
                    }

                } else {

                    // 2. If dropX is between successor mid point minus half tile width, then
                    // the node is going do replace the join.
                    if (withinYBounds(dropY, successor)) {
                        GenericComponentDrawable genericDrawable = new GenericComponentDrawable(component);
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

        return Optional.of(copy);
    }


}
