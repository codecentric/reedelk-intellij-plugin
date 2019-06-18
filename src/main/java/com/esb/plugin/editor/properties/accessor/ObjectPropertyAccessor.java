package com.esb.plugin.editor.properties.accessor;

import com.esb.plugin.component.domain.ComponentDataHolder;
import com.esb.plugin.component.domain.TypeObjectDescriptor;

public class ObjectPropertyAccessor implements PropertyAccessor {

    private final ComponentDataHolder data;
    private final String propertyName;

    public ObjectPropertyAccessor(String propertyName, ComponentDataHolder dataHolder) {
        this.propertyName = propertyName;
        this.data = dataHolder;
    }

    @Override
    public void set(Object object) {
        data.set(propertyName, new TypeObjectDescriptor.TypeObject());
    }

    @Override
    public Object get() {
        Object typeObject = data.get(propertyName);
        if (typeObject == null) {
            data.set(propertyName, new TypeObjectDescriptor.TypeObject());
        }
        return data.get(propertyName);
    }
}
