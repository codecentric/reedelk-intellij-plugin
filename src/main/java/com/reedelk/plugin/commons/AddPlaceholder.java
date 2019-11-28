package com.reedelk.plugin.commons;

import com.reedelk.plugin.editor.designer.action.remove.strategy.PlaceholderProvider;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.graph.node.ScopedGraphNode;
import com.reedelk.plugin.graph.utils.FindFirstNodeOutsideScope;

import java.util.Optional;

public class AddPlaceholder {

    private AddPlaceholder() {
    }

    public static Optional<GraphNode> to(PlaceholderProvider placeholderProvider, FlowGraph graph, ScopedGraphNode scopedGraphNode, int index) {
        Optional<GraphNode> firstNodeOutsideScope = FindFirstNodeOutsideScope.of(graph, scopedGraphNode);
        Optional<GraphNode> maybePlaceholder = placeholderProvider.get();
        return get(placeholderProvider, graph, scopedGraphNode, index, firstNodeOutsideScope, maybePlaceholder);
    }

    public static Optional<GraphNode> to(PlaceholderProvider placeholderProvider, String placeholderDescription, FlowGraph graph, ScopedGraphNode scopedGraphNode, int index) {
        Optional<GraphNode> firstNodeOutsideScope = FindFirstNodeOutsideScope.of(graph, scopedGraphNode);
        Optional<GraphNode> maybePlaceholder = placeholderProvider.get(placeholderDescription);
        return get(placeholderProvider, graph, scopedGraphNode, index, firstNodeOutsideScope, maybePlaceholder);
    }

    private static Optional<GraphNode> get(PlaceholderProvider placeholderProvider, FlowGraph graph, ScopedGraphNode scopedGraphNode, int index, Optional<GraphNode> firstNodeOutsideScope, Optional<GraphNode> maybePlaceholder) {
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
