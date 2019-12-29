package com.reedelk.plugin.commons;

import com.reedelk.component.descriptor.*;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.reedelk.component.descriptor.TypeObjectDescriptor.TypeObject;
import static com.reedelk.runtime.commons.JsonParser.Component;

public class DefaultDescriptorDataValuesFiller {

    private DefaultDescriptorDataValuesFiller() {
    }

    public static void fill(@NotNull ComponentDataHolder dataHolder,
                            @NotNull List<ComponentPropertyDescriptor> propertyDescriptors) {
        propertyDescriptors.forEach(descriptor -> {
            String propertyName = descriptor.getPropertyName();
            TypeDescriptor propertyType = descriptor.getPropertyType();

            if (propertyType instanceof TypeObjectDescriptor) {
                fillTypeObject(dataHolder, (TypeObjectDescriptor) propertyType, propertyName);
            } else {
                Object defaultValue = DefaultPropertyValue.of(descriptor);
                dataHolder.set(propertyName, defaultValue);
            }
        });
    }

    private static void fillTypeObject(@NotNull ComponentDataHolder dataHolder,
                                       @NotNull TypeObjectDescriptor propertyType,
                                       @NotNull String propertyName) {
        TypeObject nested = TypeObjectFactory.newInstance(propertyType);
        if (Shared.YES.equals(propertyType.getShared())) {
            // If the property is shareable, we initialize it with default config ref
            nested.set(Component.ref(), TypeObject.DEFAULT_CONFIG_REF);
            dataHolder.set(propertyName, nested);
        } else {
            // Recursively fill the content of this object
            dataHolder.set(propertyName, nested);
            fill(nested, propertyType.getObjectProperties());
        }
    }
}
