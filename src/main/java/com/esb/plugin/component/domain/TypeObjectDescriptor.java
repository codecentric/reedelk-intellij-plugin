package com.esb.plugin.component.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TypeObjectDescriptor implements TypeDescriptor {

    private boolean shareable;
    private List<ComponentPropertyDescriptor> objectProperties = new ArrayList<>();

    public TypeObjectDescriptor(boolean shareable, List<ComponentPropertyDescriptor> objectProperties) {
        this.shareable = shareable;
        this.objectProperties.addAll(objectProperties);
    }

    public boolean isShareable() {
        return shareable;
    }

    public List<ComponentPropertyDescriptor> getObjectProperties() {
        return objectProperties;
    }

    @Override
    public Class<?> type() {
        return TypeObject.class;
    }

    @Override
    public Object defaultValue() {
        return null;
    }

    public static class TypeObject implements ComponentDataHolder {

        private Map<String, Object> objectDataHolder = new HashMap<>();

        @Override
        public Object get(String key) {
            return objectDataHolder.get(key);
        }

        @Override
        public void set(String propertyName, Object propertyValue) {
            objectDataHolder.put(propertyName, propertyValue);
        }
    }

}
