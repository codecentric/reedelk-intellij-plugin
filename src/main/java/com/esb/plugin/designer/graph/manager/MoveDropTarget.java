package com.esb.plugin.designer.graph.manager;

import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.FlowGraphChangeAware;
import com.esb.plugin.designer.graph.FlowGraphImpl;
import com.esb.plugin.designer.graph.ScopeUtilities;
import com.esb.plugin.designer.graph.action.AddDrawableToGraph;
import com.esb.plugin.designer.graph.connector.Connector;
import com.esb.plugin.designer.graph.connector.DrawableConnector;
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

        if (graph == null) {
            graph = new FlowGraphImpl();
        }

        // 1. Copy the original graph
        FlowGraph copy = graph.copy();

        // 2. Remove the dropped node from the copy graph (connect predecessors to successors)
        List<Drawable> predecessors = graph.predecessors(dropped);
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
        scopeContainingDroppedDrawable.ifPresent(scopedDrawable -> scopedDrawable.removeFromScope(dropped));

        // 4. Add the dropped component back to the graph to the dropped position.
        FlowGraphChangeAware modifiableGraph = new FlowGraphChangeAware(copy);
        Connector connector = new DrawableConnector(modifiableGraph, dropped);
        AddDrawableToGraph componentAdder = new AddDrawableToGraph(modifiableGraph, dropPoint, connector);
        componentAdder.add();

        // 5. If the copy of the graph was changed, then update the graph
        if (modifiableGraph.isChanged()) {
            return Optional.of(modifiableGraph);
        }

        // 6. Re-add the node to the scope if the original graph was not changed.
        scopeContainingDroppedDrawable
                .ifPresent(scopedDrawable -> scopedDrawable.addToScope(dropped));

        return Optional.empty();
    }
}
