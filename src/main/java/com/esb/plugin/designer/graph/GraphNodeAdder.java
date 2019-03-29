package com.esb.plugin.designer.graph;

import com.esb.plugin.designer.Tile;
import com.esb.plugin.designer.editor.component.Component;
import com.esb.plugin.designer.graph.drawable.Drawable;
import com.esb.plugin.designer.graph.drawable.GenericComponentDrawable;
import com.esb.plugin.designer.graph.drawable.MultipathDrawable;

import java.awt.*;
import java.util.List;
import java.util.Optional;

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

        List<Drawable> precedingNodes = graph
                .nodes()
                .stream()
                .filter(drawable -> dropX > drawable.x()).collect(toList());

        for (Drawable precedingNode : precedingNodes) {

            if (precedingNode instanceof MultipathDrawable) {
                // If Multipath no bound on the actual Y axis (the bound is given by the successors
                continue; // TODO: Handle this

            } else {

                // We must check that is within Y bound of the preceding node
                if (!withinYBounds(dropY, precedingNode)) {
                    continue;
                }

                graph
                        .successors(precedingNode)
                        .stream()
                        .filter(successor -> dropX < successor.x())
                        .findFirst()
                        .ifPresent(successor -> {
                            GenericComponentDrawable genericDrawable = new GenericComponentDrawable(component);
                            copy.add(precedingNode, genericDrawable);
                            copy.add(genericDrawable, successor);
                            copy.remove(precedingNode, successor);
                        });
            }
        }

        return Optional.of(copy);
    }

    private boolean withinYBounds(int dropY, Drawable node) {
        return dropY > node.y() - Math.floorDiv(Tile.HEIGHT, 2) &&
                dropY < node.y() + Math.floorDiv(Tile.HEIGHT, 2);
    }
}
