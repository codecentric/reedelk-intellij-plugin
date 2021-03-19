package de.codecentric.reedelk.plugin.graph.utils;

import de.codecentric.reedelk.plugin.graph.FlowGraph;
import de.codecentric.reedelk.plugin.graph.node.GraphNode;
import de.codecentric.reedelk.plugin.graph.node.ScopedGraphNode;
import de.codecentric.reedelk.plugin.commons.IsScopedGraphNode;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;

public class ListLastNodesOfScope {

    private ListLastNodesOfScope() {
    }

    public static List<GraphNode> from(FlowGraph graph, ScopedGraphNode scopedGraphNode) {
        Collection<GraphNode> allNodesInScopeAndNestedScope = collectAllNodesInsideScopesFrom(scopedGraphNode);
        return allNodesInScopeAndNestedScope
                .stream()
                .filter(node -> {
                    List<GraphNode> successors = graph.successors(node);
                    if (successors.isEmpty()) {
                        return true;
                    }
                    // If exists at least one
                    return !allNodesInScopeAndNestedScope.containsAll(successors);
                })
                .collect(toList());
    }


    private static Collection<GraphNode> collectAllNodesInsideScopesFrom(ScopedGraphNode scopedGraphNode) {
        Collection<GraphNode> scope = scopedGraphNode.getScope();
        Set<GraphNode> allElements = new HashSet<>(scope);
        Set<GraphNode> nested = new HashSet<>();
        allElements.forEach(node -> {
            if (IsScopedGraphNode.of(node)) {
                nested.addAll(collectAllNodesInsideScopesFrom((ScopedGraphNode) node));
            }
        });
        allElements.addAll(nested);
        allElements.add(scopedGraphNode);
        return allElements;
    }
}
