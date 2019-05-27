package com.esb.plugin.assertion.component;

import com.esb.plugin.component.domain.ComponentPropertyDescriptor;
import com.esb.plugin.component.domain.TypeDescriptor;

import static com.esb.plugin.component.domain.ComponentPropertyDescriptor.PropertyRequired.NOT_REQUIRED;
import static com.esb.plugin.component.domain.ComponentPropertyDescriptor.PropertyRequired.REQUIRED;
import static org.assertj.core.api.Assertions.assertThat;

public class ComponentPropertyDescriptorAssertion {

    private final ComponentDescriptorAssertion parent;
    private final ComponentPropertyDescriptor propertyDescriptor;

    public ComponentPropertyDescriptorAssertion(ComponentPropertyDescriptor propertyDescriptor, ComponentDescriptorAssertion parent) {
        this.propertyDescriptor = propertyDescriptor;
        this.parent = parent;
    }

    public ComponentPropertyDescriptorAssertion withDisplayName(String expectedDisplayName) {
        assertThat(propertyDescriptor.getDisplayName()).isEqualTo(expectedDisplayName);
        return this;
    }

    public ComponentPropertyDescriptorAssertion required() {
        assertThat(propertyDescriptor.required()).isEqualTo(REQUIRED);
        return this;
    }

    public ComponentPropertyDescriptorAssertion notRequired() {
        assertThat(propertyDescriptor.required()).isEqualTo(NOT_REQUIRED);
        return this;
    }

    public ComponentPropertyDescriptorAssertion withDefaultValue(Object object) {
        assertThat(propertyDescriptor.getDefaultValue()).isEqualTo(object);
        return this;
    }

    public ComponentPropertyDescriptorAssertion withType(TypeDescriptor descriptor) {
        assertThat(propertyDescriptor.getPropertyType()).isEqualTo(descriptor);
        return this;
    }

    public ComponentDescriptorAssertion and() {
        return parent;
    }

}
