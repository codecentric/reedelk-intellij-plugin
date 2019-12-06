package com.reedelk.plugin.assertion.component;

import com.reedelk.plugin.component.domain.ComponentDescriptor;
import com.reedelk.plugin.component.domain.ComponentPropertyDescriptor;
import com.reedelk.plugin.component.domain.ComponentType;

import javax.swing.*;
import java.awt.*;
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

    public ComponentDescriptorAssertion hasIcon(Icon expectedIcon) {
        assertThat(componentDescriptor.getIcon()).isEqualTo(expectedIcon);
        return this;
    }

    public ComponentDescriptorAssertion hasImage(Image expectedImage) {
        assertThat(componentDescriptor.getImage()).isEqualTo(expectedImage);
        return this;
    }

    public ComponentDescriptorAssertion doesNotHaveProperty(String notExpectedProperty) {
        Optional<ComponentPropertyDescriptor> optionalPropertyDescriptor = componentDescriptor.getPropertyDescriptor(notExpectedProperty);
        assertThat(optionalPropertyDescriptor).isNotPresent();
        return this;
    }

    public ComponentDescriptorAssertion hasClass(ComponentType expectedClass) {
        assertThat(componentDescriptor.getComponentType()).isEqualTo(expectedClass);
        return this;
    }

    public ComponentDescriptorAssertion propertyCount(int expectedNumOfProperties) {
        assertThat(componentDescriptor.getPropertiesDescriptors()).hasSize(expectedNumOfProperties);
        return this;
    }

}
