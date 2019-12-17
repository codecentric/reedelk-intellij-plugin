package com.reedelk.plugin.graph;

import java.util.*;
import java.util.function.Consumer;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

public class DirectedGraph<T> {

    private T root;
    private Map<T, List<T>> adjacentNodesMap = new HashMap<>();

    public DirectedGraph() {
    }

    public DirectedGraph(T root) {
        this.root = root;
        addNode(this.root);
    }

    public T root() {
        return root;
    }

    public boolean isEmpty() {
        return this.adjacentNodesMap.isEmpty();
    }

    public void root(T root) {
        this.root = root;
        addNode(root);
    }

    public Collection<T> nodes() {
        return adjacentNodesMap.keySet();
    }

    public Map<T, List<T>> edges() {
        return Collections.unmodifiableMap(adjacentNodesMap);
    }

    public void addNode(T n) {
        adjacentNodesMap.putIfAbsent(n, new ArrayList<>());
    }

    public void removeNode(T n) {
        // Remove node and all outgoing edges from n
        adjacentNodesMap.remove(n);
        // Remove all incoming edges to n
        Collection<List<T>> allAdjacentNodes = adjacentNodesMap.values();
        for (List<T> adjacentNodes : allAdjacentNodes) {
            adjacentNodes.remove(n);
        }
        // Remove root if and only if the node was root
        if (root == n) {
            root = null;
        }
    }

    public void putEdge(T n1, T n2, int index) {
        checkState(adjacentNodesMap.containsKey(n1), "n1 must be already in graph in order to add an edge");
        if (!adjacentNodesMap.containsKey(n2)) {
            adjacentNodesMap.put(n2, new ArrayList<>());
        }
        adjacentNodesMap.get(n1).add(index, n2);
    }

    public void putEdge(T n1, T n2) {
        checkState(adjacentNodesMap.containsKey(n1), "n1 must be already in graph in order to add an edge");
        if (!adjacentNodesMap.containsKey(n2)) {
            adjacentNodesMap.put(n2, new ArrayList<>());
        }
        adjacentNodesMap.get(n1).add(n2);
    }

    public void removeEdgesStartingFrom(T n) {
        checkArgument(n != null, "Node (n) must not be null");
        adjacentNodesMap.put(n, new ArrayList<>());
    }

    public void removeEdge(T n1, T n2) {
        if (adjacentNodesMap.containsKey(n1)) {
            List<T> adjacentNodes = adjacentNodesMap.get(n1);
            adjacentNodes.remove(n2);
        }
    }

    public List<T> successors(T n) {
        return Collections.unmodifiableList(adjacentNodesMap.getOrDefault(n, new ArrayList<>()));
    }

    public List<T> predecessors(T n) {
        Set<Map.Entry<T, List<T>>> entries = adjacentNodesMap.entrySet();
        List<T> predecessors = new ArrayList<>();
        for (Map.Entry<T, List<T>> entry : entries) {
            List<T> adjacentNodes = entry.getValue();
            if (adjacentNodes.contains(n)) {
                predecessors.add(entry.getKey());
            }
        }
        return Collections.unmodifiableList(predecessors);
    }

    public void breadthFirstTraversal(T root, Consumer<T> visitor) {
        Set<T> visited = new LinkedHashSet<>();
        Queue<T> queue = new LinkedList<>();
        queue.add(root);
        visited.add(root);
        while (!queue.isEmpty()) {
            T n = queue.poll();
            for (T en : adjacentNodesMap.get(n)) {
                if (!visited.contains(en)) {
                    visited.add(en);
                    queue.add(en);
                }
            }
            visitor.accept(n);
        }
    }

    public DirectedGraph<T> copy() {
        DirectedGraph<T> copy;
        copy = isEmpty() ?
                new DirectedGraph<>() :
                new DirectedGraph<>(root);

        Map<T, List<T>> edges = edges();
        for (Map.Entry<T, List<T>> entry : edges.entrySet()) {
            T n1 = entry.getKey();
            copy.addNode(n1);
            for (T adjacentNode : edges.get(n1)) {
                copy.putEdge(n1, adjacentNode);
            }
        }
        return copy;
    }
}
