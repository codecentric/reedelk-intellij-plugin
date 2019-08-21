package com.reedelk.plugin.component.domain;

import com.reedelk.plugin.converter.ValueConverterFactory;
import com.reedelk.runtime.api.annotation.Default;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkState;

public class ComponentPropertyDescriptor {

    private String displayName;
    private String propertyName;
    private String defaultValue;
    private TypeDescriptor propertyType;
    private final List<AutocompleteContext> autocompleteContexts = new ArrayList<>();
    private final List<VariableDefinition> variableDefinitions = new ArrayList<>();


    private ComponentPropertyDescriptor() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getDisplayName() {
        return displayName;
    }

    @NotNull
    public String getPropertyName() {
        return propertyName;
    }

    public Object getDefaultValue() {
        return Default.USE_DEFAULT_VALUE.equals(defaultValue) ?
                propertyType.defaultValue() :
                ValueConverterFactory.forType(propertyType).from(defaultValue);
    }

    public TypeDescriptor getPropertyType() {
        return propertyType;
    }

    @NotNull
    public List<AutocompleteContext> getAutocompleteContexts() {
        return autocompleteContexts;
    }

    @NotNull
    public List<VariableDefinition> getVariableDefinitions() {
        return variableDefinitions;
    }

    public static class Builder {

        private String displayName;
        private String propertyName;
        private String defaultValue;
        private TypeDescriptor propertyType;
        private List<AutocompleteContext> autocompleteContexts = new ArrayList<>();
        private List<VariableDefinition> variableDefinitions = new ArrayList<>();

        public Builder displayName(String displayName) {
            this.displayName = displayName;
            return this;
        }

        public Builder defaultValue(String defaultValue) {
            this.defaultValue = defaultValue;
            return this;
        }

        public Builder propertyName(String propertyName) {
            this.propertyName = propertyName;
            return this;
        }

        public Builder type(TypeDescriptor type) {
            this.propertyType = type;
            return this;
        }

        public Builder autocompleteContext(AutocompleteContext autocompleteContext) {
            this.autocompleteContexts.add(autocompleteContext);
            return this;
        }

        public Builder autocompleteVariable(VariableDefinition variableDefinition) {
            this.variableDefinitions.add(variableDefinition);
            return this;
        }

        public ComponentPropertyDescriptor build() {
            checkState(propertyName != null, "property name");
            checkState(propertyType != null, "property type");

            ComponentPropertyDescriptor descriptor = new ComponentPropertyDescriptor();
            descriptor.displayName = displayName;
            descriptor.propertyName = propertyName;
            descriptor.defaultValue = defaultValue;
            descriptor.propertyType = propertyType;
            descriptor.autocompleteContexts.addAll(autocompleteContexts);
            descriptor.variableDefinitions.addAll(variableDefinitions);
            return descriptor;
        }
    }
}
