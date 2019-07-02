package com.esb.plugin.graph.layout;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.ScopedGraphNode;
import com.esb.plugin.graph.utils.FindContainingLayer;
import com.esb.plugin.graph.utils.FindScopes;
import com.esb.plugin.graph.utils.ListLastNodesOfScope;

import java.util.*;

class ComputeMaxScopesEndingInEachLayer {

    private Map<ScopedGraphNode, Integer> SCOPE_MAX_LAYER_INDEX = new HashMap<>();

    private final FlowGraph graph;
    private final List<List<GraphNode>> layers;

    ComputeMaxScopesEndingInEachLayer(FlowGraph graph, List<List<GraphNode>> layers) {
        this.graph = graph;
        this.layers = layers;
        compute();
    }

    int forLayer(int index) {
        Set<ScopedGraphNode> scopesInLayer = new HashSet<>();
        for (Map.Entry<ScopedGraphNode, Integer> entry : SCOPE_MAX_LAYER_INDEX.entrySet()) {
            if (entry.getValue() == index) {
                scopesInLayer.add(entry.getKey());
            }
        }
        int max = 0;
        for (ScopedGraphNode graphNode : scopesInLayer) {
            Stack<ScopedGraphNode> scopesBelongsTo = FindScopes.of(graph, graphNode);
            // Count amongst the ones  finishing in this layer how many are in
            scopesBelongsTo.retainAll(scopesInLayer);
            int currentCount = scopesBelongsTo.size();
            if (max < currentCount) max = currentCount;

        }
        return max;
    }

    private void compute() {
        for (List<GraphNode> layer : layers) {
            compute(layer);
        }
    }

    private void compute(List<GraphNode> layer) {
        for (GraphNode layerNode : layer) {
            if (layerNode instanceof ScopedGraphNode) {
                ScopedGraphNode scopeNode = (ScopedGraphNode) layerNode;
                ListLastNodesOfScope.from(graph, scopeNode).forEach(lastNodeOfScope -> {
                    int layerIndex = FindContainingLayer.of(layers, lastNodeOfScope);
                    if (!SCOPE_MAX_LAYER_INDEX.containsKey(scopeNode)) {
                        SCOPE_MAX_LAYER_INDEX.put(scopeNode, layerIndex);
                    }
                    if (SCOPE_MAX_LAYER_INDEX.containsKey(scopeNode) &&
                            SCOPE_MAX_LAYER_INDEX.get(scopeNode) < layerIndex) {
                        SCOPE_MAX_LAYER_INDEX.put(scopeNode, layerIndex);
                    }
                });
            }
        }
    }
}
