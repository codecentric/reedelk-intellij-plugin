package com.reedelk.plugin.component.type.flowreference.discovery;

import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.graph.node.ScopedGraphNode;
import com.reedelk.plugin.graph.utils.FindOutermostScope;
import com.reedelk.plugin.service.module.impl.component.ComponentContext;

import java.util.List;
import java.util.Optional;

public class SubflowReferenceGraphAwareContext extends ComponentContext {

    public SubflowReferenceGraphAwareContext(FlowGraph subflowGraph, GraphNode nodeWeWantOutputFrom) {
        super(subflowGraph, nodeWeWantOutputFrom);
    }

    @Override
    public Optional<ScopedGraphNode> joiningScopeOf(GraphNode target) {
        // If the target is the flow reference, then the joining scope of the
        // target must be computed by considering the end nodes of the subflow
        // referenced by the flow reference node.
        if (target == node()) {
            List<GraphNode> subflowEndNodes = graph.endNodes();
            return FindOutermostScope.of(graph, subflowEndNodes);
        } else {
            return super.joiningScopeOf(target);
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
