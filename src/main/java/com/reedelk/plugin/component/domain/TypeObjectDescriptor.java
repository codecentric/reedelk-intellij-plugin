package com.reedelk.plugin.component.domain;

import com.reedelk.runtime.commons.JsonParser;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.reedelk.plugin.component.domain.Shared.YES;

public class TypeObjectDescriptor implements TypeDescriptor {

    private final String typeFullyQualifiedName;

    private Shared shared;
    private List<ComponentPropertyDescriptor> objectProperties = new ArrayList<>();

    public TypeObjectDescriptor(@NotNull final String typeFullyQualifiedName,
                                @NotNull final List<ComponentPropertyDescriptor> objectProperties,
                                @NotNull final Shared shared) {
        this.shared = shared;
        this.typeFullyQualifiedName = typeFullyQualifiedName;
        this.objectProperties.addAll(objectProperties);
    }

    @Override
    public Class<?> type() {
        return TypeObject.class;
    }

    @Override
    public Object defaultValue() {
        return null;
    }

    public Shared getShared() {
        return shared;
    }

    public List<ComponentPropertyDescriptor> getObjectProperties() {
        return objectProperties;
    }

    // If the type object is not shared, then the serialized
    // json does not contain the fully qualified name
    public TypeObject newInstance() {
        return YES.equals(shared) ?
                new TypeObject() :
                new TypeObject(typeFullyQualifiedName);
    }

    public String getTypeFullyQualifiedName() {
        return typeFullyQualifiedName;
    }

    public static class TypeObject implements ComponentDataHolder {

        public static final String DEFAULT_CONFIG_REF = "";

        private Map<String, Object> objectDataHolder = new HashMap<>();

        // A type object defined within a flow, must have the
        // implementor fully qualified name information when serialized.
        public TypeObject(String typeFullyQualifiedName) {
            objectDataHolder.put(JsonParser.Implementor.name(), typeFullyQualifiedName);
        }

        // Config ref constructor (does not need implementor name in the serialized json)
        public TypeObject() {
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> T get(String key) {
            return (T) objectDataHolder.get(key);
        }

        @Override
        public List<String> keys() {
            return new ArrayList<>(objectDataHolder.keySet());
        }

        @Override
        public void set(String propertyName, Object propertyValue) {
            objectDataHolder.put(propertyName, propertyValue);
        }

        @Override
        public boolean has(String key) {
            return objectDataHolder.containsKey(key);
        }
    }

}
