package com.reedelk.plugin.assertion.graph;

import com.reedelk.module.descriptor.model.property.ObjectDescriptor;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

public class TypeObjectAssertion {

    private final ObjectDescriptor.TypeObject typeObject;
    private final NodeAssertion parent;

    TypeObjectAssertion(ObjectDescriptor.TypeObject typeObject, NodeAssertion parent) {
        this.typeObject = typeObject;
        this.parent = parent;
    }

    public TypeObjectAssertion hasDataWithValue(String propertyName, Object propertyValue) {
        Object actualValue = typeObject.get(propertyName);
        if (propertyValue instanceof BigDecimal) {
            int comparison = ((BigDecimal) propertyValue).compareTo((BigDecimal) actualValue);
            assertThat(comparison).isEqualTo(0);
        } else {
            assertThat(actualValue).isEqualTo(propertyValue);
        }
        return this;
    }

    public TypeObjectAssertion isEmpty() {
        assertThat(typeObject.keys()).isEmpty();
        return this;
    }

    public NodeAssertion and() {
        return parent;
    }
}
