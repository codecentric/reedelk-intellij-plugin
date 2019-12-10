package com.reedelk.plugin.component.domain;

import com.reedelk.plugin.converter.ValueConverterFactory;
import com.reedelk.runtime.api.annotation.Default;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkState;

public class ComponentPropertyDescriptor {

    private String hintValue;
    private String displayName;
    private String propertyName;
    private String defaultValue;
    private TypeDescriptor propertyType;
    private AutoCompleteContributorDefinition autoCompleteContributorDefinition;

    private final List<WhenDefinition> whenDefinitions = new ArrayList<>();


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

    @Nullable
    public String getHintValue() {
        return hintValue;
    }

    @Nullable
    public Object getDefaultValue() {
        return Default.USE_DEFAULT_VALUE.equals(defaultValue) ?
                propertyType.defaultValue() :
                ValueConverterFactory.forType(propertyType).from(defaultValue);
    }

    @NotNull
    public List<WhenDefinition> getWhenDefinitions() {
        return whenDefinitions;
    }

    public Optional<AutoCompleteContributorDefinition> getAutoCompleteContributorDefinition() {
        return Optional.ofNullable(autoCompleteContributorDefinition);
    }

    @NotNull
    @SuppressWarnings("unchecked")
    public <T extends TypeDescriptor> T getPropertyType() {
        return (T) propertyType;
    }

    public static class Builder {

        private String hintValue;
        private String displayName;
        private String propertyName;
        private String defaultValue;

        private TypeDescriptor propertyType;

        private List<WhenDefinition> whenDefinitions = new ArrayList<>();
        private AutoCompleteContributorDefinition autoCompleteContributionsDefinition;

        public Builder type(TypeDescriptor type) {
            this.propertyType = type;
            return this;
        }

        public Builder hintValue(String hintValue) {
            this.hintValue = hintValue;
            return this;
        }

        public Builder displayName(String displayName) {
            this.displayName = displayName;
            return this;
        }

        public Builder propertyName(String propertyName) {
            this.propertyName = propertyName;
            return this;
        }

        public Builder defaultValue(String defaultValue) {
            this.defaultValue = defaultValue;
            return this;
        }

        public Builder when(WhenDefinition whenDefinition) {
            this.whenDefinitions.add(whenDefinition);
            return this;
        }

        public Builder autoComplete(AutoCompleteContributorDefinition definition) {
            this.autoCompleteContributionsDefinition = definition;
            return this;
        }

        public ComponentPropertyDescriptor build() {
            checkState(propertyName != null, "property name");
            checkState(propertyType != null, "property type");

            ComponentPropertyDescriptor descriptor = new ComponentPropertyDescriptor();
            descriptor.hintValue = hintValue;
            descriptor.displayName = displayName;
            descriptor.propertyName = propertyName;
            descriptor.defaultValue = defaultValue;
            descriptor.propertyType = propertyType;
            descriptor.whenDefinitions.addAll(whenDefinitions);
            descriptor.autoCompleteContributorDefinition = autoCompleteContributionsDefinition;
            return descriptor;
        }
    }
}
