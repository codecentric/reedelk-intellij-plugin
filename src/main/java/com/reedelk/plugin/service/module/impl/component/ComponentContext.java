package com.reedelk.plugin.service.module.impl.component;

import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.graph.node.ScopedGraphNode;
import com.reedelk.plugin.graph.utils.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.Stack;

public class ComponentContext {

    private final GraphNode node;
    protected final FlowGraph graph;

    public ComponentContext(@NotNull FlowGraph graph, @Nullable GraphNode node) {
        this.node = node;
        this.graph = graph;
    }

    public GraphNode node() {
        return node;
    }

    public List<GraphNode> endNodes() {
        return graph.endNodes();
    }

    public List<GraphNode> successors(GraphNode target) {
        return graph.successors(target);
    }

    public List<GraphNode> predecessors(GraphNode target) {
        return graph.predecessors(target);
    }

    public Optional<ScopedGraphNode> joiningScopeOf(GraphNode target) {
        return FindJoiningScope.of(graph, target);
    }

    public Optional<GraphNode> findFirstNodeOutsideCurrentScope(ScopedGraphNode graphNode) {
        return FindFirstNodeOutsideCurrentScope.of(graph, graphNode);
    }

    public Optional<ScopedGraphNode> outermostScopeOf(List<GraphNode> targets) {
        // TODO: There is a function already written for this, oterwise create one.
        if (targets.isEmpty()) return Optional.empty();
        GraphNode target = targets.get(0);
        Stack<ScopedGraphNode> stack = FindScopes.of(graph, target);
        ScopedGraphNode current = null;
        while (!stack.isEmpty()) {
            current = stack.pop();
        }
        return Optional.ofNullable(current);
    }

    public List<GraphNode> listLastNodesOfScope(ScopedGraphNode scopedGraphNode) {
        return ListLastNodesOfScope.from(graph, scopedGraphNode);
    }

    public Optional<ScopedGraphNode> findScopeOf(GraphNode predecessor) {
        return FindScope.of(graph, predecessor);
    }
}
