package com.reedelk.plugin.assertion.graph;

import com.reedelk.component.descriptor.TypeObjectDescriptor;
import com.reedelk.plugin.component.ComponentData;
import com.reedelk.plugin.graph.node.GraphNode;

import java.math.BigDecimal;

import static com.reedelk.plugin.assertion.component.ComponentDataValueMatchers.ComponentDataValueMatcher;
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
        if (propertyValue instanceof BigDecimal) {
            int comparison = ((BigDecimal) propertyValue).compareTo((BigDecimal) actualValue);
            assertThat(comparison).isEqualTo(0);
        } else {
            assertThat(actualValue).isEqualTo(propertyValue);
        }
        return this;
    }

    public NodeAssertion hasDataWithValue(String propertyName, ComponentDataValueMatcher matcher) {
        ComponentData component = this.node.componentData();
        Object actualValue = component.get(propertyName);
        assertThat(matcher.matches(actualValue)).isTrue();
        return this;
    }

    public TypeObjectAssertion hasTypeObject(String propertyName) {
        ComponentData component = this.node.componentData();
        Object actualValue = component.get(propertyName);
        return new TypeObjectAssertion((TypeObjectDescriptor.TypeObject) actualValue, this);
    }

    public FlowGraphAssertion and() {
        return parent;
    }

}
