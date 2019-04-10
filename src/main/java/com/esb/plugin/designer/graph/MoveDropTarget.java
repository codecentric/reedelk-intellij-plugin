package com.esb.plugin.designer.graph;

import com.esb.plugin.designer.graph.drawable.Drawable;
import com.esb.plugin.designer.graph.drawable.ScopedDrawable;

import java.awt.*;
import java.util.List;
import java.util.Optional;

public class MoveDropTarget {

    /**
     * Called when we drop a drawable because we are moving it.
     */
    public Optional<FlowGraph> drop(int x, int y, FlowGraph graph, Drawable dropped) {
        Point dropPoint = new Point(x, y);

        // Steps when we drop:
        if (graph == null) {
            graph = new FlowGraphImpl();
        }

        // 1. Copy the original graph
        FlowGraph copy = graph.copy();

        // 2. Remove the dropped node from the copy graph
        // Get the predecessors of the node and connect it to the successors
        java.util.List<Drawable> predecessors = graph.predecessors(dropped);
        List<Drawable> successors = graph.successors(dropped);
        if (predecessors.isEmpty()) {
            copy.root(successors.get(0));
        } else {
            for (Drawable predecessor : predecessors) {
                for (Drawable successor : successors) {
                    copy.add(predecessor, successor);
                }
            }
        }

        copy.remove(dropped);

        // 3. Remove the dropped node from any scope it might belong to
        Optional<ScopedDrawable> scopeContainingDroppedDrawable = ScopeUtilities.findScope(copy, dropped);
        scopeContainingDroppedDrawable
                .ifPresent(scopedDrawable -> scopedDrawable.removeFromScope(dropped));

        FlowGraphChangeAware modifiableGraph = new FlowGraphChangeAware(copy);

        // 4. Add the dropped component back to the graph to the dropped position.
        AddDrawableToGraph componentAdder = new AddDrawableToGraph(modifiableGraph, dropPoint, dropped);
        componentAdder.add();

        // 5. If the copy of the graph was changed, then update the graph
        if (modifiableGraph.isChanged()) {
            return Optional.of(modifiableGraph);
        }
        // Re-add the node to the scope if nothing was changed.
        scopeContainingDroppedDrawable
                .ifPresent(scopedDrawable -> scopedDrawable.addToScope(dropped));

        return Optional.empty();
    }
}
