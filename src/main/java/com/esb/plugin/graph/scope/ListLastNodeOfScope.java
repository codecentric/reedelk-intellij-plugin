package com.esb.plugin.graph.scope;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.ScopedNode;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;

public class ListLastNodeOfScope {

    public static List<GraphNode> from(FlowGraph graph, ScopedNode scopedNode) {
        Collection<GraphNode> allDrawablesInScopeAndNestedScope = collectAllDrawablesInsideScopesFrom(scopedNode);
        return allDrawablesInScopeAndNestedScope.stream().filter(drawable -> {
            List<GraphNode> successors = graph.successors(drawable);
            if (successors.isEmpty()) return true;
            // If exists at least one
            return !allDrawablesInScopeAndNestedScope.containsAll(successors);
        }).collect(toList());
    }


    private static Collection<GraphNode> collectAllDrawablesInsideScopesFrom(ScopedNode scopedNode) {
        Collection<GraphNode> scope = scopedNode.getScope();
        Set<GraphNode> allElements = new HashSet<>(scope);
        Set<GraphNode> nested = new HashSet<>();
        allElements.forEach(drawable -> {
            if (drawable instanceof ScopedNode) {
                nested.addAll(collectAllDrawablesInsideScopesFrom((ScopedNode) drawable));
            }
        });
        allElements.addAll(nested);
        allElements.add(scopedNode);
        return allElements;
    }
}
