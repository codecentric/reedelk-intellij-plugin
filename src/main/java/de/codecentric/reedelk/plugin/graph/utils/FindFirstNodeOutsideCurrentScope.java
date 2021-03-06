package de.codecentric.reedelk.plugin.graph.utils;

import de.codecentric.reedelk.plugin.graph.FlowGraph;
import de.codecentric.reedelk.plugin.graph.node.GraphNode;
import de.codecentric.reedelk.plugin.graph.node.ScopedGraphNode;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.google.common.base.Preconditions.checkState;

/**
 * It finds the first node outside the given ScopedGraphNode.
 * If the scope is wrapped by another scope and there is no
 * node between current scope and wrapping scope, then
 * an empty optional is returned.
 * <p>
 * Please not that by definition a scope must be followed by
 * zero or at most one node.
 */
public class FindFirstNodeOutsideCurrentScope {

    private FindFirstNodeOutsideCurrentScope() {
    }

    public static Optional<GraphNode> of(FlowGraph graph, ScopedGraphNode target) {

        Optional<ScopedGraphNode> scopeContainingTarget = FindScope.of(graph, target);

        Set<GraphNode> firstNodesOutsideScope = new HashSet<>();

        ListLastNodesOfScope.from(graph, target)
                .forEach(lastNodeOfScope -> {
                    List<GraphNode> successors = graph.successors(lastNodeOfScope);
                    // If the target does not belong to any scope,
                    // then we can add all successors, since they
                    // don't belong to any scope.
                    if (!scopeContainingTarget.isPresent()) {
                        firstNodesOutsideScope.addAll(successors);

                    } else {
                        ScopedGraphNode wrappingScope = scopeContainingTarget.get();
                        successors.forEach(successor -> {
                            if (wrappingScope.scopeContains(successor)) {
                                firstNodesOutsideScope.add(successor);
                            }
                        });
                    }
                });

        checkState(firstNodesOutsideScope.isEmpty() ||
                        firstNodesOutsideScope.size() == 1,
                "First node outside scope must be absent or at most one");

        return firstNodesOutsideScope.stream().findFirst();
    }

}
