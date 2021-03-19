package de.codecentric.reedelk.plugin.graph.layout;

import de.codecentric.reedelk.plugin.graph.FlowGraph;
import de.codecentric.reedelk.plugin.graph.node.GraphNode;
import de.codecentric.reedelk.plugin.graph.node.ScopedGraphNode;
import de.codecentric.reedelk.plugin.graph.utils.FindContainingLayer;
import de.codecentric.reedelk.plugin.graph.utils.FindScopes;
import de.codecentric.reedelk.plugin.graph.utils.ListLastNodesOfScope;
import de.codecentric.reedelk.plugin.commons.IsScopedGraphNode;
import org.jetbrains.annotations.NotNull;

import java.util.*;

class ComputeMaxScopesEndingInEachLayer {

    private final Map<ScopedGraphNode, Integer> scopeEndNodeMaxLayerIndexMap = new HashMap<>();

    private final FlowGraph graph;
    private final List<List<GraphNode>> layers;

    ComputeMaxScopesEndingInEachLayer(FlowGraph graph, List<List<GraphNode>> layers) {
        this.graph = graph;
        this.layers = layers;
        compute();
    }

    int forLayer(int index) {
        Set<ScopedGraphNode> scopesInLayer = getScopesInLayer(index);
        int max = 0;
        for (ScopedGraphNode graphNode : scopesInLayer) {
            Stack<ScopedGraphNode> scopesBelongsTo = FindScopes.of(graph, graphNode);

            // We need to find the total number of nested
            // scopes for only those belonging to this layer.
            scopesBelongsTo.retainAll(scopesInLayer);

            int currentCount = scopesBelongsTo.size();
            if (max < currentCount) max = currentCount;
        }
        return max;
    }

    @NotNull
    private Set<ScopedGraphNode> getScopesInLayer(int index) {
        Set<ScopedGraphNode> scopesInLayer = new HashSet<>();
        for (Map.Entry<ScopedGraphNode, Integer> entry : scopeEndNodeMaxLayerIndexMap.entrySet()) {
            if (entry.getValue() == index) {
                scopesInLayer.add(entry.getKey());
            }
        }
        return scopesInLayer;
    }

    private void compute() {
        for (List<GraphNode> layer : layers) {
            compute(layer);
        }
    }

    private void compute(List<GraphNode> layer) {
        for (GraphNode layerNode : layer) {
            if (IsScopedGraphNode.of(layerNode)) {
                ScopedGraphNode scopeNode = (ScopedGraphNode) layerNode;
                ListLastNodesOfScope.from(graph, scopeNode).forEach(lastNodeOfScope -> {
                    int layerIndex = FindContainingLayer.of(layers, lastNodeOfScope);
                    if (!scopeEndNodeMaxLayerIndexMap.containsKey(scopeNode)) {
                        scopeEndNodeMaxLayerIndexMap.put(scopeNode, layerIndex);
                    }
                    if (scopeEndNodeMaxLayerIndexMap.containsKey(scopeNode) &&
                            scopeEndNodeMaxLayerIndexMap.get(scopeNode) < layerIndex) {
                        scopeEndNodeMaxLayerIndexMap.put(scopeNode, layerIndex);
                    }
                });
            }
        }
    }
}
