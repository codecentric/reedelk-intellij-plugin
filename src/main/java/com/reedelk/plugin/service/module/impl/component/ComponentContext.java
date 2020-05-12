package com.reedelk.plugin.service.module.impl.component;

import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.graph.node.ScopedGraphNode;
import com.reedelk.plugin.graph.utils.FindJoiningScope;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class ComponentContext {

    protected final FlowGraph graph;
    private final GraphNode node;

    public ComponentContext(@NotNull FlowGraph graph, @Nullable GraphNode node) {
        this.node = node;
        this.graph = graph;
    }

    public GraphNode node() {
        return node;
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
}
