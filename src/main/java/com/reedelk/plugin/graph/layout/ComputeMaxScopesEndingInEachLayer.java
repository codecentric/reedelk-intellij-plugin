package com.reedelk.plugin.graph.layout;

import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.graph.node.ScopedGraphNode;
import com.reedelk.plugin.graph.utils.FindContainingLayer;
import com.reedelk.plugin.graph.utils.FindScopes;
import com.reedelk.plugin.graph.utils.ListLastNodesOfScope;
import org.jetbrains.annotations.NotNull;

import java.util.*;

class ComputeMaxScopesEndingInEachLayer {

    private Map<ScopedGraphNode, Integer> SCOPE_END_NODE_MAX_LAYER_INDEX = new HashMap<>();

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
        for (Map.Entry<ScopedGraphNode, Integer> entry : SCOPE_END_NODE_MAX_LAYER_INDEX.entrySet()) {
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
            if (layerNode instanceof ScopedGraphNode) {
                ScopedGraphNode scopeNode = (ScopedGraphNode) layerNode;
                ListLastNodesOfScope.from(graph, scopeNode).forEach(lastNodeOfScope -> {
                    int layerIndex = FindContainingLayer.of(layers, lastNodeOfScope);
                    if (!SCOPE_END_NODE_MAX_LAYER_INDEX.containsKey(scopeNode)) {
                        SCOPE_END_NODE_MAX_LAYER_INDEX.put(scopeNode, layerIndex);
                    }
                    if (SCOPE_END_NODE_MAX_LAYER_INDEX.containsKey(scopeNode) &&
                            SCOPE_END_NODE_MAX_LAYER_INDEX.get(scopeNode) < layerIndex) {
                        SCOPE_END_NODE_MAX_LAYER_INDEX.put(scopeNode, layerIndex);
                    }
                });
            }
        }
    }
}
