package com.esb.plugin.editor.designer.action;

import com.esb.plugin.commons.Half;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.FlowGraphChangeAware;
import com.esb.plugin.graph.FlowSnapshot;
import com.esb.plugin.graph.action.ActionNodeAdd;
import com.esb.plugin.graph.action.ActionNodeRemove;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.NothingSelectedNode;
import com.esb.plugin.graph.node.ScopedGraphNode;
import com.esb.plugin.graph.utils.FindScope;
import com.intellij.openapi.module.Module;

import java.awt.*;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;

public class MoveActionHandler {

    private final Module module;
    private final Point movePoint;
    private final GraphNode selected;
    private final Graphics2D graphics;
    private final FlowSnapshot snapshot;
    private final ActionNodeAdd actionNodeAdd;

    public MoveActionHandler(Module module, FlowSnapshot snapshot, Graphics2D graphics, GraphNode selectedNode, Point movePoint, ActionNodeAdd actionNodeAdd) {
        checkArgument(module != null, "module");
        checkArgument(snapshot != null, "snapshot");
        checkArgument(graphics != null, "graphics");
        checkArgument(selectedNode != null, "selected node");

        this.module = module;
        this.snapshot = snapshot;
        this.graphics = graphics;
        this.movePoint = movePoint;
        this.selected = selectedNode;
        this.actionNodeAdd = actionNodeAdd;
    }

    public void handle() {
        int dragX = movePoint.x;
        int dragY = movePoint.y;

        // If outside the current selected area, then we consider the drop as effective.
        // Create a method inside selected to check if given coordinates are within hover area.
        // this logic should be encapsulated there
        boolean withinX =
                dragX > selected.x() - Half.of(selected.width(graphics)) &&
                        dragX < selected.x() + Half.of(selected.width(graphics));

        boolean withinY =
                dragY > selected.y() - Half.of(selected.height(graphics)) &&
                        dragY < selected.y() + Half.of(selected.height(graphics));

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

        // 4. Add the node
        actionNodeAdd.execute(modifiableGraph);

        // 5. If the copy of the graph was changed, then update the graph
        // TODO: IF REMOVED BUT NOT ADDED, then should not be changed... but now if we remove, and then we cannot add the node for some reason the snapshot is still updated which is not necessary
        if (modifiableGraph.isChanged()) {
            modifiableGraph.commit(module);
            snapshot.updateSnapshot(this, modifiableGraph);

        } else {
            // 6. Add back the node to the scope if the original graph was not changed.
            selectedScope.ifPresent(scopedNode -> scopedNode.addToScope(selected));
        }
    }
}
