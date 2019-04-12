package com.esb.plugin.designer.graph.manager;

import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.FlowGraphChangeAware;
import com.esb.plugin.designer.graph.FlowGraphImpl;
import com.esb.plugin.designer.graph.ScopeUtilities;
import com.esb.plugin.designer.graph.action.AddDrawableToGraph;
import com.esb.plugin.designer.graph.action.RemoveDrawableFromGraph;
import com.esb.plugin.designer.graph.connector.Connector;
import com.esb.plugin.designer.graph.connector.DrawableConnector;
import com.esb.plugin.designer.graph.drawable.Drawable;
import com.esb.plugin.designer.graph.drawable.ScopedDrawable;

import java.awt.*;
import java.util.Optional;

public class MoveDropTarget {

    /**
     * Called when we drop a drawable because we are moving it.
     */
    public Optional<FlowGraph> drop(int x, int y, FlowGraph graph, Drawable dropped) {

        if (graph == null) {
            graph = new FlowGraphImpl();
        }

        // 1. Copy the original graph
        FlowGraph copy = graph.copy();

        // 2. Remove the dropped node from the copy graph
        RemoveDrawableFromGraph componentRemover = new RemoveDrawableFromGraph(copy, dropped);
        componentRemover.remove();

        // 3. Remove the dropped node from any scope it might belong to
        Optional<ScopedDrawable> droppedDrawableScope = ScopeUtilities.findScope(copy, dropped);
        droppedDrawableScope.ifPresent(scopedDrawable -> scopedDrawable.removeFromScope(dropped));

        // 4. Add the dropped component back to the graph to the dropped position.
        Point dropPoint = new Point(x, y);
        FlowGraphChangeAware updatedGraph = addDrawableToGraph(copy, dropped, dropPoint);

        // 5. If the copy of the graph was changed, then update the graph
        if (updatedGraph.isChanged()) {
            return Optional.of(updatedGraph);

        } else {
            // 6. Add back the node to the scope if the original graph was not changed.
            droppedDrawableScope.ifPresent(scopedDrawable -> scopedDrawable.addToScope(dropped));

            // 7. Returning empty since nothing was changed
            return Optional.empty();
        }
    }

    private FlowGraphChangeAware addDrawableToGraph(FlowGraph graph, Drawable dropped, Point dropPoint) {
        FlowGraphChangeAware modifiableGraph = new FlowGraphChangeAware(graph);
        Connector connector = new DrawableConnector(modifiableGraph, dropped);
        AddDrawableToGraph componentAdder = new AddDrawableToGraph(modifiableGraph, dropPoint, connector);
        componentAdder.add();
        return modifiableGraph;
    }
}
