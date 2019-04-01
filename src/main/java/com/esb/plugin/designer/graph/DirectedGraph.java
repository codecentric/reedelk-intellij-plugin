package com.esb.plugin.designer.graph;

import java.util.*;
import java.util.function.Consumer;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

public class DirectedGraph<NodeType> {

    private final NodeType root;
    private Map<NodeType, List<NodeType>> adjacentNodesMap = new HashMap<>();

    public DirectedGraph(NodeType root) {
        this.root = root;
        addNode(this.root);
    }

    public NodeType root() {
        return root;
    }

    public Collection<NodeType> nodes() {
        return adjacentNodesMap.keySet();
    }

    public Map<NodeType, List<NodeType>> edges() {
        return Collections.unmodifiableMap(adjacentNodesMap);
    }

    public void addNode(NodeType n) {
        adjacentNodesMap.putIfAbsent(n, new ArrayList<>());
    }

    public void removeNode(NodeType n) {
        adjacentNodesMap.remove(n);
        Collection<List<NodeType>> allAdjacentNodes = adjacentNodesMap.values();
        for (List<NodeType> adjacentNodes : allAdjacentNodes) {
            adjacentNodes.remove(n);
        }
    }

    public void putEdge(NodeType n1, NodeType n2) {
        checkState(adjacentNodesMap.containsKey(n1), "n1 must be already in graph in order to add an edge");
        if (!adjacentNodesMap.containsKey(n2)) {
            adjacentNodesMap.put(n2, new ArrayList<>());
        }
        adjacentNodesMap.get(n1).add(n2);
    }

    public void removeEdgesStartingFrom(NodeType n) {
        checkArgument(n != null, "Node (n) must not be null");
        adjacentNodesMap.put(n, new ArrayList<>());
    }

    public void removeEdge(NodeType n1, NodeType n2) {
        if (adjacentNodesMap.containsKey(n1)) {
            List<NodeType> adjacentNodes = adjacentNodesMap.get(n1);
            adjacentNodes.remove(n2);
        }
    }

    public List<NodeType> successors(NodeType n) {
        return Collections.unmodifiableList(adjacentNodesMap.getOrDefault(n, new ArrayList<>()));
    }

    public List<NodeType> predecessors(NodeType n) {
        Set<Map.Entry<NodeType, List<NodeType>>> entries = adjacentNodesMap.entrySet();
        List<NodeType> predecessors = new ArrayList<>();
        for (Map.Entry<NodeType, List<NodeType>> entry : entries) {
            List<NodeType> adjacentNodes = entry.getValue();
            if (adjacentNodes.contains(n)) {
                predecessors.add(entry.getKey());
            }
        }
        return Collections.unmodifiableList(predecessors);
    }

    public void breadthFirstTraversal(NodeType root, Consumer<NodeType> visitor) {
        Set<NodeType> visited = new LinkedHashSet<>();
        Queue<NodeType> queue = new LinkedList<>();
        queue.add(root);
        visited.add(root);
        while (!queue.isEmpty()) {
            NodeType n = queue.poll();
            for (NodeType en : adjacentNodesMap.get(n)) {
                if (!visited.contains(en)) {
                    visited.add(en);
                    queue.add(en);
                }
            }
            visitor.accept(n);
        }
    }

    public Optional<NodeType> commonSuccessor(NodeType n1, NodeType n2) {
        Collection<NodeType> n1Descendants = descendants(n1);
        List<NodeType> successors = successors(n2);
        for (NodeType successor : successors) {
            if (n1Descendants.contains(successor)) {
                return Optional.of(successor);
            }
        }
        for (NodeType successor : successors) {
            Optional<NodeType> commonSuccessor = commonSuccessor(n1, successor);
            if (commonSuccessor.isPresent()) {
                return commonSuccessor;
            }
        }
        return Optional.empty();
    }

    private Collection<NodeType> descendants(NodeType n1) {
        List<NodeType> descendants = new ArrayList<>();
        for (NodeType descendant : successors(n1)) {
            descendants.add(descendant);
            descendants.addAll(descendants(descendant));
        }
        return descendants;
    }

    public DirectedGraph<NodeType> copy() {
        DirectedGraph<NodeType> copy = new DirectedGraph<>(root);
        Map<NodeType, List<NodeType>> edges = edges();
        for (Map.Entry<NodeType, List<NodeType>> entry : edges.entrySet()) {
            NodeType n1 = entry.getKey();
            copy.addNode(n1);
            for (NodeType adjacentNode : edges.get(n1)) {
                copy.putEdge(n1, adjacentNode);
            }
        }
        return copy;
    }

}
