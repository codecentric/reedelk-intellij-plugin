package com.esb.plugin.graph.utils;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.ScopedGraphNode;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;

public class ListLastNodeOfScope {

    public static List<GraphNode> from(FlowGraph graph, ScopedGraphNode scopedGraphNode) {
        Collection<GraphNode> allDrawablesInScopeAndNestedScope = collectAllDrawablesInsideScopesFrom(scopedGraphNode);
        return allDrawablesInScopeAndNestedScope
                .stream()
                .filter(drawable -> {

                    List<GraphNode> successors = graph.successors(drawable);

                    if (successors.isEmpty()) return true;

                    // If exists at least one
                    return !allDrawablesInScopeAndNestedScope.containsAll(successors);

                }).collect(toList());
    }


    private static Collection<GraphNode> collectAllDrawablesInsideScopesFrom(ScopedGraphNode scopedGraphNode) {
        Collection<GraphNode> scope = scopedGraphNode.getScope();
        Set<GraphNode> allElements = new HashSet<>(scope);
        Set<GraphNode> nested = new HashSet<>();

        allElements.forEach(drawable -> {
            if (drawable instanceof ScopedGraphNode) {
                nested.addAll(collectAllDrawablesInsideScopesFrom((ScopedGraphNode) drawable));
            }
        });

        allElements.addAll(nested);
        allElements.add(scopedGraphNode);

        return allElements;
    }
}
