package com.esb.plugin.editor.designer.action;

import com.esb.plugin.commons.Half;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.FlowGraphChangeAware;
import com.esb.plugin.graph.FlowSnapshot;
import com.esb.plugin.graph.action.Action;
import com.esb.plugin.graph.action.remove.ActionNodeRemove;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.GraphNodeFactory;
import com.esb.plugin.graph.node.ScopedGraphNode;
import com.esb.plugin.graph.utils.FindScope;
import com.esb.system.component.Placeholder;
import com.intellij.openapi.module.Module;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Optional;

public class MoveActionHandler {

    private final FlowSnapshot snapshot;
    private final Graphics2D graphics;
    private final GraphNode selected;
    private final Action actionAdd;
    private final Point movePoint;
    private final Module module;

    public MoveActionHandler(@NotNull Module module,
                             @NotNull FlowSnapshot snapshot,
                             @NotNull Graphics2D graphics,
                             @NotNull GraphNode selectedNode,
                             @NotNull Point movePoint,
                             @NotNull Action actionAdd) {
        this.selected = selectedNode;
        this.actionAdd = actionAdd;
        this.movePoint = movePoint;
        this.snapshot = snapshot;
        this.graphics = graphics;
        this.module = module;
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

        // 1. Copy the original graph
        FlowGraph copy = snapshot.getGraph().copy();

        // 2. Remove the dropped node from the copy graph
        FlowGraphChangeAware modifiableGraph = new FlowGraphChangeAware(copy);

        ActionNodeRemove componentRemover = new ActionNodeRemove(() ->
                GraphNodeFactory.get(module, Placeholder.class.getName()),
                selected);
        componentRemover.execute(modifiableGraph);

        // 3. Remove the dropped node from any scope it might belong to
        Optional<ScopedGraphNode> selectedScope = FindScope.of(modifiableGraph, selected);
        selectedScope.ifPresent(scopedNode -> scopedNode.removeFromScope(selected));

        // 4. Add the node
        actionAdd.execute(modifiableGraph);

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
