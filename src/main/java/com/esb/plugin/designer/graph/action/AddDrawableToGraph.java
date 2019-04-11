package com.esb.plugin.designer.graph.action;


import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.connector.Connector;
import com.esb.plugin.designer.graph.drawable.Drawable;
import com.esb.plugin.designer.graph.drawable.ScopedDrawable;

import java.awt.*;
import java.util.Optional;

import static com.esb.plugin.designer.graph.action.AddDrawableToGraphUtilities.findClosestPrecedingDrawable;
import static com.esb.plugin.designer.graph.action.AddDrawableToGraphUtilities.isReplacingRoot;
import static com.google.common.base.Preconditions.checkState;

/**
 * Adds to the graph a new node representing the Component Name to the given location.
 * This class find the best position where to place the node in the Graph given the drop point location.
 */
public class AddDrawableToGraph {

    private final FlowGraph graph;
    private final Point dropPoint;
    private final Connector connector;

    public AddDrawableToGraph(FlowGraph graph, Point dropPoint, Connector connector) {
        this.dropPoint = dropPoint;
        this.graph = graph;
        this.connector = connector;
    }

    public void add() {
        // First Drawable added to the canvas (it is root)
        if (graph.isEmpty()) {
            connector.addPredecessor(null);

            // Check if we are replacing the first (root) node.
        } else if (isReplacingRoot(graph, dropPoint)) {
            connector.add();
            connector.addSuccessor(graph.root());
            connector.root();

        } else {

            Optional<Drawable> precedingDrawable = findClosestPrecedingDrawable(graph, dropPoint);

            if (precedingDrawable.isPresent()) {

                AddStrategy strategy;

                Drawable closestPrecedingDrawable = precedingDrawable.get();

                if (closestPrecedingDrawable instanceof ScopedDrawable) {
                    strategy = new PrecedingScopedDrawable(graph, dropPoint, connector);

                } else if (graph.successors(closestPrecedingDrawable).isEmpty()) {
                    strategy = new PrecedingDrawableWithoutSuccessor(graph, dropPoint, connector);

                } else {
                    // Only ScopedDrawable nodes might have multiple successors. In all other cases
                    // a node in the flow must have at most one successor.
                    checkState(graph.successors(closestPrecedingDrawable).size() == 1,
                            "Successors size MUST be 1, otherwise it must be a Scoped Drawable");
                    strategy = new PrecedingDrawableWithOneSuccessor(graph, dropPoint, connector);
                }

                strategy.execute(precedingDrawable.get());
            }
        }
    }

}
