package com.reedelk.plugin.assertion.graph;

import com.reedelk.plugin.component.domain.ComponentData;
import com.reedelk.plugin.graph.node.GraphNode;

import static org.assertj.core.api.Assertions.assertThat;

public class NodeAssertion {

    private final GraphNode node;
    private final FlowGraphAssertion parent;

    NodeAssertion(GraphNode node, FlowGraphAssertion parent) {
        this.parent = parent;
        this.node = node;
    }

    public NodeAssertion is(GraphNode expectedNode) {
        assertThat(this.node).isEqualTo(expectedNode);
        return this;
    }

    public NodeAssertion hasDataWithValue(String propertyName, Object propertyValue) {
        ComponentData component = this.node.componentData();
        Object actualValue = component.get(propertyName);
        assertThat(actualValue).isEqualTo(propertyValue);
        return this;
    }

    public FlowGraphAssertion and() {
        return parent;
    }

}
