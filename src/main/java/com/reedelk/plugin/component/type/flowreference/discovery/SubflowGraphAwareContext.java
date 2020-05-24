package com.reedelk.plugin.component.type.flowreference.discovery;

import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.graph.node.ScopedGraphNode;
import com.reedelk.plugin.graph.utils.FindJoiningScope;
import com.reedelk.plugin.graph.utils.FindScopes;
import com.reedelk.plugin.service.module.impl.component.ComponentContext;

import java.util.List;
import java.util.Optional;
import java.util.Stack;

public class SubflowGraphAwareContext extends ComponentContext {

    public SubflowGraphAwareContext(FlowGraph subflowGraph, GraphNode nodeWeWantOutputFrom) {
        super(subflowGraph, nodeWeWantOutputFrom);
    }

    // The subflow ends with a scoped graph node? Then, the joining
    // scope of the target, must be computed by considering the
    // end nodes of the current subflow.
    @Override
    public Optional<ScopedGraphNode> joiningScopeOf(GraphNode target) {
        List<GraphNode> graphNodes = graph.endNodes();
        if (graphNodes.size() > 1) {
            Stack<ScopedGraphNode> of = FindScopes.of(graph, graphNodes.get(1));
            ScopedGraphNode last = of.pop();
            while (graphNodes.isEmpty()) {
                last = of.pop();
            }
            return Optional.of(last);
        } else {
            return FindJoiningScope.of(graph, target);
        }
    }

    @Override
    public List<GraphNode> predecessors(GraphNode target) {
        // If we ask for the predecessors of the flow reference,
        // we need to return the last nodes of the referenced subflow.
        // In any other case we return the predecessors of the target node.
        return target == node() ?
                graph.endNodes() :
                graph.predecessors(target);
    }
}
