package com.esb.plugin.commons;

import com.esb.plugin.component.domain.ComponentDataHolder;
import com.esb.plugin.component.domain.ComponentPropertyDescriptor;
import com.esb.plugin.component.domain.TypeDescriptor;
import com.esb.plugin.component.domain.TypeObjectDescriptor;

import java.util.List;

import static com.esb.internal.commons.JsonParser.Component;
import static com.esb.plugin.component.domain.TypeObjectDescriptor.TypeObject;

public class DefaultDescriptorDataValuesFiller {

    public static void fill(ComponentDataHolder dataHolder, List<ComponentPropertyDescriptor> propertyDescriptors) {
        propertyDescriptors.forEach(descriptor -> {

            String propertyName = descriptor.getPropertyName();
            TypeDescriptor propertyType = descriptor.getPropertyType();

            if (propertyType instanceof TypeObjectDescriptor) {
                fillTypeObject(dataHolder, propertyName, (TypeObjectDescriptor) propertyType);

            } else {
                Object defaultValue = descriptor.getDefaultValue();
                dataHolder.set(propertyName, defaultValue);
            }
        });
    }

    private static void fillTypeObject(ComponentDataHolder dataHolder, String propertyName, TypeObjectDescriptor propertyType) {
        if (propertyType.isShareable()) {
            // If the property is shareable, we initialize it with default config ref
            TypeObject nested = propertyType.newInstance();
            nested.set(Component.configRef(), TypeObject.DEFAULT_CONFIG_REF);
            dataHolder.set(propertyName, nested);

        } else {
            // Recursively fill the content of this object
            TypeObject typeObject = propertyType.newInstance();
            dataHolder.set(propertyName, typeObject);
            fill(typeObject, propertyType.getObjectProperties());
        }
    }
}
