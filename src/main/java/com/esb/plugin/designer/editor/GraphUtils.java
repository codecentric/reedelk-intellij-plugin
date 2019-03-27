package com.esb.plugin.designer.editor;

import com.google.common.graph.Graph;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Given a Graph it computes the max number of nodes at the same level.
 */
public class GraphUtils {

    public static <N> int maxNodesCountAtAnyLevel(Graph<N> graph, N root) {
        if (root == null) return 0;

        HashMap<Integer, Integer> levelMaxNodesMap = new HashMap<>();
        levelMaxNodesMap.put(0, 1);
        maxNodesCountAtAnyLevel(graph, root, 1, levelMaxNodesMap);
        return levelMaxNodesMap.values().stream().mapToInt(v -> v).max().getAsInt();
    }

    private static <N> void maxNodesCountAtAnyLevel(Graph<N> graph, N current, int level, Map<Integer, Integer> levelMaxNodesMap) {
        Set<N> adjacentNodes = graph.successors(current);
        int nodesCount = adjacentNodes.size();
        if (levelMaxNodesMap.containsKey(level)) {
            levelMaxNodesMap.put(level, levelMaxNodesMap.get(level) + nodesCount);
        } else {
            levelMaxNodesMap.put(level, nodesCount);
        }
        adjacentNodes.forEach(n ->
                maxNodesCountAtAnyLevel(graph, n, level + 1, levelMaxNodesMap));
    }

}
