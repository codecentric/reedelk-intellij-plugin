package com.reedelk.plugin.commons;

import com.reedelk.plugin.component.domain.ComponentDataHolder;
import com.reedelk.plugin.component.domain.ComponentPropertyDescriptor;
import com.reedelk.plugin.component.domain.TypeDescriptor;
import com.reedelk.plugin.component.domain.TypeObjectDescriptor;

import java.util.List;

import static com.reedelk.plugin.component.domain.TypeObjectDescriptor.TypeObject;
import static com.reedelk.runtime.commons.JsonParser.Component;

public class DefaultDescriptorDataValuesFiller {

    private DefaultDescriptorDataValuesFiller() {
    }

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
        TypeObject nested = propertyType.newInstance();
        if (propertyType.isShareable()) {
            // If the property is shareable, we initialize it with default config ref
            nested.set(Component.configRef(), TypeObject.DEFAULT_CONFIG_REF);
            dataHolder.set(propertyName, nested);

        } else {
            // Recursively fill the content of this object
            dataHolder.set(propertyName, nested);
            fill(nested, propertyType.getObjectProperties());
        }
    }
}
