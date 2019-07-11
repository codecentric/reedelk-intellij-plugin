package com.esb.plugin.component.domain;

import com.esb.plugin.component.scanner.AutocompleteContext;
import com.esb.plugin.component.scanner.AutocompleteVariable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkState;

public class ComponentPropertyDescriptor {

    private String displayName;
    private String propertyName;
    private Object defaultValue;
    private PropertyRequired required;
    private TypeDescriptor propertyType;
    private final List<AutocompleteContext> autocompleteContexts = new ArrayList<>();
    private final List<AutocompleteVariable> autocompleteVariables = new ArrayList<>();

    public enum PropertyRequired {
        REQUIRED,
        NOT_REQUIRED
    }

    private ComponentPropertyDescriptor() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean required() {
        return PropertyRequired.REQUIRED.equals(required);
    }

    @NotNull
    public String getPropertyName() {
        return propertyName;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public TypeDescriptor getPropertyType() {
        return propertyType;
    }

    @NotNull
    public List<AutocompleteContext> getAutocompleteContexts() {
        return autocompleteContexts;
    }

    @NotNull
    public List<AutocompleteVariable> getAutocompleteVariables() {
        return autocompleteVariables;
    }

    public static class Builder {

        private String displayName;
        private String propertyName;
        private Object defaultValue;
        private TypeDescriptor propertyType;
        private PropertyRequired required = PropertyRequired.NOT_REQUIRED;
        private List<AutocompleteContext> autocompleteContexts = new ArrayList<>();
        private List<AutocompleteVariable> autocompleteVariables = new ArrayList<>();

        public Builder displayName(String displayName) {
            this.displayName = displayName;
            return this;
        }

        public Builder defaultValue(Object defaultValue) {
            this.defaultValue = defaultValue;
            return this;
        }

        public Builder propertyName(String propertyName) {
            this.propertyName = propertyName;
            return this;
        }

        public Builder required(PropertyRequired required) {
            this.required = required;
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

        public Builder autocompleteVariables(AutocompleteVariable autocompleteVariable) {
            this.autocompleteVariables.add(autocompleteVariable);
            return this;
        }

        public ComponentPropertyDescriptor build() {
            checkState(propertyName != null, "property name");
            checkState(propertyType != null, "property type");

            ComponentPropertyDescriptor descriptor = new ComponentPropertyDescriptor();
            descriptor.required = required;
            descriptor.displayName = displayName;
            descriptor.propertyName = propertyName;
            descriptor.defaultValue = defaultValue;
            descriptor.propertyType = propertyType;
            descriptor.autocompleteContexts.addAll(autocompleteContexts);
            descriptor.autocompleteVariables.addAll(autocompleteVariables);
            return descriptor;
        }
    }
}
