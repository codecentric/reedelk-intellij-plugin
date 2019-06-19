package com.esb.plugin.editor.properties.accessor;

import com.esb.plugin.component.domain.ComponentDataHolder;

public class ObjectPropertyAccessor implements PropertyAccessor {

    private final ComponentDataHolder data;
    private final String propertyName;

    public ObjectPropertyAccessor(String propertyName, ComponentDataHolder dataHolder) {
        this.propertyName = propertyName;
        this.data = dataHolder;
    }

    @Override
    public void set(Object object) {
        data.set(propertyName, object);
    }

    @Override
    public Object get() {
        return data.get(propertyName);
    }
}
