package com.esb.plugin.designer.editor.designer;

import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.FlowGraphChangeAware;
import com.esb.plugin.designer.graph.FlowGraphImpl;
import com.esb.plugin.designer.graph.ScopeUtilities;
import com.esb.plugin.designer.graph.action.RemoveDrawableFromGraph;
import com.esb.plugin.designer.graph.drawable.Drawable;
import com.esb.plugin.designer.graph.drawable.ScopedDrawable;
import com.esb.plugin.designer.graph.drawable.decorators.NothingSelectedDrawable;

import java.awt.*;
import java.util.Optional;

class MoveActionHandler extends AbstractActionHandler {

    private final FlowGraph graph;
    private final Point movePoint;
    private final Drawable selected;
    private final Graphics2D graphics;

    MoveActionHandler(FlowGraph graph, Graphics2D graphics, Drawable selected, Point movePoint) {
        this.graph = graph;
        this.graphics = graphics;
        this.movePoint = movePoint;
        this.selected = selected;
    }

    Optional<FlowGraph> handle() {
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

        if (selected instanceof NothingSelectedDrawable) {
            return Optional.empty();
        }

        // 1. Copy the original graph
        FlowGraph copy = graph == null ? new FlowGraphImpl() : graph.copy();

        // 2. Remove the dropped node from the copy graph
        RemoveDrawableFromGraph componentRemover = new RemoveDrawableFromGraph(copy, selected);
        componentRemover.remove();

        // 3. Remove the dropped node from any scope it might belong to
        Optional<ScopedDrawable> selectedScope = ScopeUtilities.findScope(copy, selected);
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
