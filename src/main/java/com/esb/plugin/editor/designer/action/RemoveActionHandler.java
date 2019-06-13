package com.esb.plugin.editor.designer.action;

import com.esb.plugin.graph.FlowGraphChangeAware;
import com.esb.plugin.graph.GraphSnapshot;
import com.esb.plugin.graph.action.ActionNodeRemove;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.ScopedGraphNode;
import com.esb.plugin.graph.utils.FindScope;
import com.intellij.openapi.module.Module;

import java.util.Optional;

public class RemoveActionHandler {

    private final GraphNode nodeToRemove;
    private final GraphSnapshot snapshot;
    private final Module module;

    public RemoveActionHandler(Module module, GraphSnapshot snapshot, GraphNode nodeToRemove) {
        this.nodeToRemove = nodeToRemove;
        this.snapshot = snapshot;
        this.module = module;
    }

    public void handle() {
        FlowGraphChangeAware modifiableGraph = new FlowGraphChangeAware(snapshot.getGraph().copy());

        // 1. Remove the node
        ActionNodeRemove componentRemover = new ActionNodeRemove(modifiableGraph, nodeToRemove);
        componentRemover.remove();

        // 2. Remove the node from any scope it might belong to
        Optional<ScopedGraphNode> selectedScope = FindScope.of(modifiableGraph, nodeToRemove);
        selectedScope.ifPresent(scopedNode -> scopedNode.removeFromScope(nodeToRemove));

        if (modifiableGraph.isChanged()) {
            modifiableGraph.commit(module);
            snapshot.updateSnapshot(this, modifiableGraph);
        } else {
            // 3. Add back the node to the scope if the original graph was not changed.
            selectedScope.ifPresent(scopedNode -> scopedNode.addToScope(nodeToRemove));
        }
    }
}
