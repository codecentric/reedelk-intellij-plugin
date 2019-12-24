package com.reedelk.plugin.assertion.component;

import com.reedelk.plugin.component.domain.AutoCompleteContributorDefinition;
import com.reedelk.plugin.component.domain.ComponentPropertyDescriptor;
import com.reedelk.plugin.component.domain.ScriptSignatureDefinition;
import com.reedelk.plugin.component.domain.TypeDescriptor;

import java.util.Optional;

import static com.reedelk.plugin.assertion.component.AutoCompleteContributorDefinitionMatchers.AutoCompleteContributorDefinitionMatcher;
import static com.reedelk.plugin.assertion.component.ScriptSignatureDefinitionMatchers.ScriptSignatureDefinitionMatcher;
import static com.reedelk.plugin.assertion.component.TypeDescriptorMatchers.TypeDescriptorMatcher;
import static org.assertj.core.api.Assertions.assertThat;

public class ComponentPropertyDescriptorAssertion {

    private final ComponentPropertyDescriptor propertyDescriptor;

    public ComponentPropertyDescriptorAssertion(ComponentPropertyDescriptor propertyDescriptor) {
        this.propertyDescriptor = propertyDescriptor;
    }

    public ComponentPropertyDescriptorAssertion hasName(String expectedName) {
        assertThat(propertyDescriptor.getPropertyName()).isEqualTo(expectedName);
        return this;
    }

    public ComponentPropertyDescriptorAssertion hasDisplayName(String expectedDisplayName) {
        assertThat(propertyDescriptor.getDisplayName()).isEqualTo(expectedDisplayName);
        return this;
    }

    public ComponentPropertyDescriptorAssertion hasDefaultValue(Object object) {
        assertThat(propertyDescriptor.getDefaultValue()).isEqualTo(object);
        return this;
    }

    public ComponentPropertyDescriptorAssertion hasType(TypeDescriptorMatcher matcher) {
        TypeDescriptor propertyType = propertyDescriptor.getPropertyType();
        assertThat(matcher.matches(propertyType)).isTrue();
        return this;
    }

    public ComponentPropertyDescriptorAssertion hasAutoCompleteContributorDefinition(AutoCompleteContributorDefinitionMatcher matcher) {
        Optional<AutoCompleteContributorDefinition> definition = propertyDescriptor.getAutoCompleteContributorDefinition();
        assertThat(definition).isPresent();
        assertThat(matcher.matches(definition.get())).isTrue();
        return this;
    }

    public ComponentPropertyDescriptorAssertion hasNotAutoCompleteContributorDefinition() {
        Optional<AutoCompleteContributorDefinition> definition = propertyDescriptor.getAutoCompleteContributorDefinition();
        assertThat(definition).isNotPresent();
        return this;
    }

    public ComponentPropertyDescriptorAssertion hasScriptSignatureDefinition(ScriptSignatureDefinitionMatcher matcher) {
        Optional<ScriptSignatureDefinition> definition = propertyDescriptor.getScriptSignatureDefinition();
        assertThat(definition).isPresent();
        assertThat(matcher.matches(definition.get())).isTrue();
        return this;
    }

    public ComponentPropertyDescriptorAssertion hasNotScriptSignatureDefinition() {
        Optional<ScriptSignatureDefinition> definition = propertyDescriptor.getScriptSignatureDefinition();
        assertThat(definition).isNotPresent();
        return this;
    }

    public ComponentPropertyDescriptorAssertion hasPropertyInfo(String propertyInfoText) {
        Optional<String> optionalPropertyInfo = propertyDescriptor.getPropertyInfo();
        assertThat(optionalPropertyInfo).isPresent();
        assertThat(optionalPropertyInfo).hasValue(propertyInfoText);
        return this;
    }
}