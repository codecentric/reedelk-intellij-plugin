package com.esb.plugin.editor.designer.action;

import com.esb.plugin.graph.FlowGraphChangeAware;
import com.esb.plugin.graph.FlowSnapshot;
import com.esb.plugin.graph.action.remove.ActionNodeRemove;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.GraphNodeFactory;
import com.esb.plugin.graph.node.ScopedGraphNode;
import com.esb.plugin.graph.utils.FindScope;
import com.esb.system.component.Placeholder;
import com.intellij.openapi.module.Module;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

public class RemoveActionHandler {

    private final GraphNode nodeToRemove;
    private final Module module;
    private final FlowSnapshot snapshot;
    private final ActionNodeRemove.PlaceholderProvider placeholderProvider;

    public RemoveActionHandler(Module module, FlowSnapshot snapshot, GraphNode nodeToRemove) {
        this.nodeToRemove = nodeToRemove;
        this.snapshot = snapshot;
        this.module = module;
        this.placeholderProvider = () -> GraphNodeFactory.get(module, Placeholder.class.getName());
    }


    private void removeNestedNodes(FlowGraphChangeAware modifiableGraph, ScopedGraphNode scopedGraphNode) {
        Collection<GraphNode> scope = scopedGraphNode.getScope();
        Collection<GraphNode> copyOfScope = new ArrayList<>(scope);
        copyOfScope.forEach(node -> {
            if (node instanceof ScopedGraphNode) {
                removeNestedNodes(modifiableGraph, (ScopedGraphNode) node);
            } else {
                ActionNodeRemove componentRemover = new ActionNodeRemove(placeholderProvider, modifiableGraph, node);
                componentRemover.remove();
            }
        });
        ActionNodeRemove componentRemover = new ActionNodeRemove(placeholderProvider, modifiableGraph, scopedGraphNode);
        componentRemover.remove();
    }

    public void handle() {
        FlowGraphChangeAware modifiableGraph = new FlowGraphChangeAware(snapshot.getGraph().copy());

        // First remove all the nodes belonging to this scope.
        if (nodeToRemove instanceof ScopedGraphNode) {
            removeNestedNodes(modifiableGraph, (ScopedGraphNode) nodeToRemove);
        }

        // 1. Remove the node
        ActionNodeRemove componentRemover = new ActionNodeRemove(placeholderProvider, modifiableGraph, nodeToRemove);
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
