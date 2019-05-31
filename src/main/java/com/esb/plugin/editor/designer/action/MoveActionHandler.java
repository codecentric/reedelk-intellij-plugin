package com.esb.plugin.editor.designer.action;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.FlowGraphChangeAware;
import com.esb.plugin.graph.GraphSnapshot;
import com.esb.plugin.graph.action.ActionNodeRemove;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.NothingSelectedNode;
import com.esb.plugin.graph.node.ScopedGraphNode;
import com.esb.plugin.graph.utils.FindScope;
import com.intellij.openapi.module.Module;

import java.awt.*;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;

public class MoveActionHandler extends AbstractActionHandler {

    private final Point movePoint;
    private final GraphNode selected;
    private final Graphics2D graphics;
    private final GraphSnapshot snapshot;

    public MoveActionHandler(Module module, GraphSnapshot snapshot, Graphics2D graphics, GraphNode selectedNode, Point movePoint) {
        super(module);
        checkArgument(snapshot != null, "snapshot");
        checkArgument(graphics != null, "graphics");
        checkArgument(selectedNode != null, "selected node");

        this.snapshot = snapshot;
        this.graphics = graphics;
        this.selected = selectedNode;
        this.movePoint = movePoint;
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
        FlowGraph copy = snapshot.getGraph().copy();

        // 2. Remove the dropped node from the copy graph
        FlowGraphChangeAware modifiableGraph = new FlowGraphChangeAware(copy);

        ActionNodeRemove componentRemover = new ActionNodeRemove(modifiableGraph, selected);
        componentRemover.remove();

        // 3. Remove the dropped node from any scope it might belong to
        Optional<ScopedGraphNode> selectedScope = FindScope.of(modifiableGraph, selected);
        selectedScope.ifPresent(scopedNode -> scopedNode.removeFromScope(selected));

        // 4. Add the dropped component back to the graph to the dropped position.
        Point dropPoint = new Point(dragX, dragY);
        addNodeToGraph(modifiableGraph, selected, dropPoint, graphics);

        // 5. If the copy of the graph was changed, then update the graph
        // TODO: IF REMOVED BUT NOT ADDED, then should not be changed... but now if we remove, and then we cannot add the node for some reason the snapshot is still updated which is not necessary
        if (modifiableGraph.isChanged()) {
            modifiableGraph.commit();
            snapshot.updateSnapshot(this, modifiableGraph);

        } else {
            // 6. Add back the node to the scope if the original graph was not changed.
            selectedScope.ifPresent(scopedNode -> scopedNode.addToScope(selected));
        }
    }
}
