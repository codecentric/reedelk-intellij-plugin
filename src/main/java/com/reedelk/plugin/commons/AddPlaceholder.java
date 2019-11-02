package com.reedelk.plugin.commons;

import com.reedelk.plugin.component.type.placeholder.PlaceholderNode;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.action.remove.strategy.PlaceholderProvider;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.graph.node.ScopedGraphNode;
import com.reedelk.plugin.graph.utils.FindFirstNodeOutsideScope;

import java.util.Optional;

public class AddPlaceholder {

    public static Optional<? extends GraphNode> to(PlaceholderProvider placeholderProvider, FlowGraph graph, ScopedGraphNode scopedGraphNode, int index) {
        Optional<GraphNode> firstNodeOutsideScope = FindFirstNodeOutsideScope.of(graph, scopedGraphNode);
        Optional<PlaceholderNode> maybePlaceholder = placeholderProvider.get();
        maybePlaceholder.ifPresent(placeholder -> {
            graph.add(placeholder);
            graph.add(scopedGraphNode, placeholder, index);
            scopedGraphNode.addToScope(placeholder);
            firstNodeOutsideScope.ifPresent(node -> {
                graph.remove(scopedGraphNode, node);
                graph.add(placeholder, node);
            });
        });
        return placeholderProvider.get();
    }
}
