package com.esb.plugin.assertion;

import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.ScopedGraphNode;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

public class ScopedNodeAssertion extends NodeAssertion {


    private final ScopedGraphNode node;
    private final GraphAssertion parent;

    public ScopedNodeAssertion(ScopedGraphNode node, GraphAssertion parent) {
        super(node, parent);
        this.parent = parent;
        this.node = node;
    }

    public ScopedNodeAssertion scopeContainsExactly(GraphNode... expectedScopeNodes) {
        Collection<GraphNode> scope = this.node.getScope();
        assertThat(scope).containsExactlyInAnyOrder(expectedScopeNodes);
        return this;
    }

    public GraphAssertion and() {
        return parent;
    }

}
