package com.esb.plugin.designer.canvas;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.FlowGraphChangeAware;
import com.esb.plugin.graph.FlowGraphImpl;
import com.esb.plugin.graph.GraphNode;
import com.esb.plugin.graph.action.RemoveNodeAction;
import com.esb.plugin.graph.drawable.ScopedDrawable;
import com.esb.plugin.graph.drawable.decorators.NothingSelectedNode;
import com.esb.plugin.graph.scope.FindScope;
import com.intellij.openapi.module.Module;

import java.awt.*;
import java.util.Optional;

public class MoveActionHandler extends AbstractActionHandler {

    private final FlowGraph graph;
    private final Point movePoint;
    private final GraphNode selected;
    private final Graphics2D graphics;

    public MoveActionHandler(Module module, FlowGraph graph, Graphics2D graphics, GraphNode selected, Point movePoint) {
        super(module);
        this.graph = graph;
        this.graphics = graphics;
        this.movePoint = movePoint;
        this.selected = selected;
    }

    public Optional<FlowGraph> handle() {
        int dragX = movePoint.x;
        int dragY = movePoint.y;

        // If outside the current selected area, then we consider the drop as effective.
        // Create a method inside selected to check if given coordinates are within hover area.
        // this logic should be encapsulated there
        boolean withinX =
                dragX > selected.x() - Math.floorDiv(selected.width(graphics), 2) &&
                        dragX < selected.x() + Math.floorDiv(selected.width(graphics), 2);

        boolean withinY =
                dragY > selected.y() - Math.floorDiv(selected.height(graphics), 2) &&
                        dragY < selected.y() + Math.floorDiv(selected.height(graphics), 2);

        if (withinX && withinY) {
            return Optional.empty();
        }

        if (selected instanceof NothingSelectedNode) {
            return Optional.empty();
        }

        // 1. Copy the original graph
        FlowGraph copy = graph == null ? new FlowGraphImpl() : graph.copy();

        // 2. Remove the dropped node from the copy graph
        RemoveNodeAction componentRemover = new RemoveNodeAction(copy, selected);
        componentRemover.remove();

        // 3. Remove the dropped node from any scope it might belong to
        Optional<ScopedDrawable> selectedScope = FindScope.of(copy, selected);
        selectedScope.ifPresent(scopedDrawable -> scopedDrawable.removeFromScope(selected));

        // 4. Add the dropped component back to the graph to the dropped position.
        Point dropPoint = new Point(dragX, dragY);
        FlowGraphChangeAware updatedGraph = addDrawableToGraph(copy, selected, dropPoint, graphics);

        // 5. If the copy of the graph was changed, then update the graph
        if (updatedGraph.isChanged()) {
            return Optional.of(updatedGraph);

        } else {
            // 6. Add back the node to the scope if the original graph was not changed.
            selectedScope.ifPresent(scopedDrawable -> scopedDrawable.addToScope(selected));
        }

        return Optional.empty();
    }
}
