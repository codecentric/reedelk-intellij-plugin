package com.reedelk.plugin.assertion.graph;

import java.math.BigDecimal;

import static com.reedelk.module.descriptor.model.TypeObjectDescriptor.TypeObject;
import static org.assertj.core.api.Assertions.assertThat;

public class TypeObjectAssertion {

    private final TypeObject typeObject;
    private final NodeAssertion parent;

    TypeObjectAssertion(TypeObject typeObject, NodeAssertion parent) {
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
