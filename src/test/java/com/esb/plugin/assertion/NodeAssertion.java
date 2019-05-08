package com.esb.plugin.assertion;

import com.esb.plugin.component.ComponentData;
import com.esb.plugin.graph.node.GraphNode;

import static org.assertj.core.api.Assertions.assertThat;

public class NodeAssertion {

    private final GraphNode node;
    private final GraphAssertion parent;

    NodeAssertion(GraphNode node, GraphAssertion parent) {
        this.parent = parent;
        this.node = node;
    }

    public NodeAssertion is(GraphNode expectedNode) {
        assertThat(this.node).isEqualTo(expectedNode);
        return this;
    }

    public NodeAssertion hasDataWithValue(String propertyName, Object propertyValue) {
        ComponentData component = this.node.component();
        assertThat(component.get(propertyName)).isEqualTo(propertyValue);
        return this;
    }

    public GraphAssertion and() {
        return parent;
    }

}
