package de.codecentric.reedelk.plugin.assertion.graph;

import de.codecentric.reedelk.plugin.graph.node.GraphNode;
import de.codecentric.reedelk.plugin.graph.node.ScopedGraphNode;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

public class ScopedNodeAssertion extends NodeAssertion {


    private final ScopedGraphNode node;
    private final FlowGraphAssertion parent;

    public ScopedNodeAssertion(ScopedGraphNode node, FlowGraphAssertion parent) {
        super(node, parent);
        this.parent = parent;
        this.node = node;
    }

    public ScopedNodeAssertion scopeContainsExactly(GraphNode... expectedScopeNodes) {
        Collection<GraphNode> scope = this.node.getScope();
        assertThat(scope).containsExactlyInAnyOrder(expectedScopeNodes);
        return this;
    }

    public ScopedNodeAssertion scopeIsEmpty() {
        Collection<GraphNode> scope = this.node.getScope();
        assertThat(scope).isEmpty();
        return this;
    }

    public FlowGraphAssertion and() {
        return parent;
    }

}
