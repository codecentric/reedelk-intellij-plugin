package com.esb.plugin.component.domain;

import com.esb.internal.commons.JsonParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TypeObjectDescriptor implements TypeDescriptor {

    private final String typeFullyQualifiedName;
    private boolean shareable;
    private List<ComponentPropertyDescriptor> objectProperties = new ArrayList<>();

    public TypeObjectDescriptor(final String typeFullyQualifiedName, final boolean shareable, final List<ComponentPropertyDescriptor> objectProperties) {
        this.shareable = shareable;
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


    public boolean isShareable() {
        return shareable;
    }

    public List<ComponentPropertyDescriptor> getObjectProperties() {
        return objectProperties;
    }

    public TypeObject newInstance() {
        return shareable ?
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
        private TypeObject(String typeFullyQualifiedName) {
            objectDataHolder.put(JsonParser.Implementor.name(), typeFullyQualifiedName);
        }

        // Config ref constructor (does not need implementor name in the serialized json)
        private TypeObject() {
        }

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
    }

}
