package com.esb.plugin.component.domain;

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
        return new TypeObject(typeFullyQualifiedName);
    }

    public String getTypeFullyQualifiedName() {
        return typeFullyQualifiedName;
    }

    public static class TypeObject implements ComponentDataHolder {

        private final String typeFullyQualifiedName;
        private Map<String, Object> objectDataHolder = new HashMap<>();

        private TypeObject(String typeFullyQualifiedName) {
            this.typeFullyQualifiedName = typeFullyQualifiedName;
        }

        @Override
        public Object get(String key) {
            return objectDataHolder.get(key);
        }

        @Override
        public List<String> keys() {
            return new ArrayList<>(objectDataHolder.keySet());
        }

        @Override
        public void set(String propertyName, Object propertyValue) {
            objectDataHolder.put(propertyName, propertyValue);
        }

        public String getTypeFullyQualifiedName() {
            return typeFullyQualifiedName;
        }
    }

}
