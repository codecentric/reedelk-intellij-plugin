package com.esb.plugin.designer.canvas.action;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.FlowGraphChangeAware;
import com.esb.plugin.graph.FlowGraphImpl;
import com.esb.plugin.graph.GraphSnapshot;
import com.esb.plugin.graph.action.RemoveNodeAction;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.NothingSelectedNode;
import com.esb.plugin.graph.node.ScopedGraphNode;
import com.esb.plugin.graph.utils.FindScope;
import com.intellij.openapi.module.Module;

import java.awt.*;
import java.util.Optional;

public class MoveActionHandler extends AbstractActionHandler {

    private final GraphSnapshot snapshot;
    private final Point movePoint;
    private final GraphNode selected;
    private final Graphics2D graphics;

    public MoveActionHandler(Module module, GraphSnapshot snapshot, Graphics2D graphics, GraphNode selected, Point movePoint) {
        super(module);
        this.snapshot = snapshot;
        this.graphics = graphics;
        this.movePoint = movePoint;
        this.selected = selected;
    }

    public void handle() {
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

        if (withinX && withinY) return;

        if (selected instanceof NothingSelectedNode) return;

        // 1. Copy the original graph
        FlowGraph copy = snapshot.getGraph() == null ? new FlowGraphImpl() : snapshot.getGraph().copy();

        // 2. Remove the dropped node from the copy graph
        RemoveNodeAction componentRemover = new RemoveNodeAction(copy, selected);
        componentRemover.remove();

        // 3. Remove the dropped node from any scope it might belong to
        Optional<ScopedGraphNode> selectedScope = FindScope.of(copy, selected);
        selectedScope.ifPresent(scopedDrawable -> scopedDrawable.removeFromScope(selected));

        // 4. Add the dropped component back to the graph to the dropped position.
        Point dropPoint = new Point(dragX, dragY);
        FlowGraphChangeAware updatedGraph = addNodeToGraph(copy, selected, dropPoint, graphics);

        // 5. If the copy of the graph was changed, then update the graph
        if (updatedGraph.isChanged()) {
            snapshot.updateSnapshot(this, updatedGraph);

        } else {
            // 6. Add back the node to the scope if the original graph was not changed.
            selectedScope.ifPresent(scopedDrawable -> scopedDrawable.addToScope(selected));
        }
    }
}
