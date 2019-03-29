package com.esb.plugin.designer.graph;

import com.esb.plugin.designer.Tile;
import com.esb.plugin.designer.editor.component.Component;
import com.esb.plugin.designer.graph.drawable.Drawable;
import com.esb.plugin.designer.graph.drawable.GenericComponentDrawable;
import com.esb.plugin.designer.graph.drawable.MultipathDrawable;

import java.awt.*;
import java.util.List;
import java.util.Optional;

import static com.esb.internal.commons.Preconditions.checkState;
import static java.util.stream.Collectors.toList;

public class GraphNodeAdder {

    /**
     * Adds to the graph a new node representing the Component Name to the given location.
     * The returned graph is a copy. An empty optional is returned if the node could not be
     * added.
     */
    public Optional<FlowGraph> add(FlowGraph graph, Point location, String componentName) {

        Component component = new Component(componentName);
        component.setDescription("A description");

        int dropX = location.x;
        int dropY = location.y;

        FlowGraph copy = graph.copy(); // TODO: Handle adding first component (root)

        // Another strategy
        // Find preceding nodes.
        // Amongst the preceding nodes, find the closest on the Y axis.
        // If the closest is MultipathDrawable:
        //  - Handle it by finding the right position
        // Otherwise:
        //  - If current is MultipathDrawableContext and successor is NOT, then it must be size one
        //  - if it is in the first half


        List<Drawable> precedingNodes = graph
                .nodes()
                .stream()
                .filter(node -> dropX > node.x() && dropX < node.x() + Tile.WIDTH)
                .collect(toList());

        int min = Integer.MAX_VALUE;
        Drawable closestPrecedingNode = null;
        for (Drawable precedingNode : precedingNodes) {
            int y = precedingNode.y();
            int delta = Math.abs(y - dropY);
            if (delta < min) {
                closestPrecedingNode = precedingNode;
                min = delta;
            }
        }
        checkState(closestPrecedingNode != null, "Closest node could not be null");

        if (closestPrecedingNode instanceof MultipathDrawable) {

        } else {
            List<Drawable> successors = graph.successors(closestPrecedingNode);
            checkState(successors.size() == 1, "Must be of size 1");

            Drawable successor = successors.get(0);

            if (withinYBounds(dropY, closestPrecedingNode)) {
                GenericComponentDrawable genericDrawable = new GenericComponentDrawable(component);
                copy.add(closestPrecedingNode, genericDrawable);
                copy.add(genericDrawable, successor);
                copy.remove(closestPrecedingNode, successor);
            }
        }


        /**
         GenericComponentDrawable genericDrawable = new GenericComponentDrawable(component);

        for (Drawable precedingNode : precedingNodes) {

            if (precedingNode instanceof MultipathDrawable) {
         // If Multipath no bound on the actual Y axis (the bound is given by the successors)

                continue; // TODO: Handle this

            } else {

         graph.successors(precedingNode)
                        .stream()
                        .filter(successor -> dropX < successor.x())
                        .findFirst()
                        .ifPresent(successor -> {


         List<Drawable> predecessors = graph.predecessors(successor);
         if (predecessors.size() == 1) {
         // We must check that is within Y bound of the preceding node
         if (withinYBounds(dropY, precedingNode)) {
         copy.add(precedingNode, genericDrawable);
         copy.add(genericDrawable, successor);
         copy.remove(precedingNode, successor);
         }

         } else {

         boolean secondHalf = Math.abs(dropX - successor.x()) < Math.floorDiv(Tile.WIDTH, 2);
         if (secondHalf) {
         predecessors.forEach(predecessor -> {
         copy.add(predecessor, genericDrawable);
         copy.remove(predecessor, successor);
         });
         copy.add(genericDrawable, successor);
         } else {
         // Find on which predecessor we need to connect
         predecessors.forEach(predecessor -> {
         // We must check that is within Y bound of the preceding node
         if (withinYBounds(dropY, predecessor)) {
         if (!copy.nodes().contains(genericDrawable)) {
         copy.add(predecessor, genericDrawable);
         copy.add(genericDrawable, successor);
         copy.remove(predecessor, successor);
         }
         }
         });
         }
         }
                        });
            }
         }*/

        return Optional.of(copy);
    }

    private boolean withinYBounds(int dropY, Drawable node) {
        return dropY > node.y() - Math.floorDiv(Tile.HEIGHT, 2) &&
                dropY < node.y() + Math.floorDiv(Tile.HEIGHT, 2);
    }
}
