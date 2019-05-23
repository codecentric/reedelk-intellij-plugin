package com.esb.plugin.assertion.component;

import com.esb.plugin.component.domain.ComponentDescriptor;
import com.esb.plugin.component.domain.ComponentPropertyDescriptor;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class ComponentDescriptorAssertion {

    private final ComponentDescriptor componentDescriptor;

    public ComponentDescriptorAssertion(ComponentDescriptor componentDescriptor) {
        this.componentDescriptor = componentDescriptor;
    }

    public ComponentDescriptorAssertion hasDisplayName(String expectedDisplayName) {
        assertThat(componentDescriptor.getDisplayName()).isEqualTo(expectedDisplayName);
        return this;
    }

    public ComponentDescriptorAssertion hasFullyQualifiedName(String expectedFullyQualifiedName) {
        assertThat(componentDescriptor.getFullyQualifiedName()).isEqualTo(expectedFullyQualifiedName);
        return this;
    }

    public ComponentDescriptorAssertion isHidden() {
        assertThat(componentDescriptor.isHidden()).isTrue();
        return this;
    }

    public ComponentDescriptorAssertion isNotHidden() {
        assertThat(componentDescriptor.isHidden()).isFalse();
        return this;
    }

    public ComponentPropertyDescriptorAssertion hasProperty(String expectedProperty) {
        Optional<ComponentPropertyDescriptor> optionalPropertyDescriptor = componentDescriptor.getPropertyDescriptor(expectedProperty);
        assertThat(optionalPropertyDescriptor).isPresent();
        return new ComponentPropertyDescriptorAssertion(optionalPropertyDescriptor.get());
    }

}