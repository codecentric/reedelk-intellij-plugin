package com.esb.plugin.assertion.graph;

import com.esb.plugin.component.domain.ComponentData;
import com.esb.plugin.graph.node.GraphNode;

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
        assertThat(component.get(propertyName)).isEqualTo(propertyValue);
        return this;
    }

    public FlowGraphAssertion and() {
        return parent;
    }

}
