package de.codecentric.reedelk.plugin.assertion.graph;

import de.codecentric.reedelk.module.descriptor.model.property.ObjectDescriptor;
import de.codecentric.reedelk.plugin.assertion.component.ComponentDataValueMatchers;
import de.codecentric.reedelk.plugin.component.ComponentData;
import de.codecentric.reedelk.plugin.graph.node.GraphNode;

import java.math.BigDecimal;

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

    public NodeAssertion hasDataWithValue(String propertyName, ComponentDataValueMatchers.ComponentDataValueMatcher matcher) {
        ComponentData component = this.node.componentData();
        Object actualValue = component.get(propertyName);
        assertThat(matcher.matches(actualValue)).isTrue();
        return this;
    }

    public TypeObjectAssertion hasTypeObject(String propertyName) {
        ComponentData component = this.node.componentData();
        Object actualValue = component.get(propertyName);
        return new TypeObjectAssertion((ObjectDescriptor.TypeObject) actualValue, this);
    }

    public FlowGraphAssertion and() {
        return parent;
    }

}
