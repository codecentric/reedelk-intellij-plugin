package com.reedelk.plugin.commons;

import com.reedelk.plugin.component.domain.ComponentDataHolder;
import com.reedelk.plugin.component.domain.ComponentPropertyDescriptor;
import com.reedelk.plugin.component.domain.TypeDescriptor;
import com.reedelk.plugin.component.domain.TypeObjectDescriptor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.reedelk.plugin.component.domain.Shared.YES;
import static com.reedelk.plugin.component.domain.TypeObjectDescriptor.TypeObject;
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
                Object defaultValue = descriptor.getDefaultValue();
                dataHolder.set(propertyName, defaultValue);
            }
        });
    }

    private static void fillTypeObject(@NotNull ComponentDataHolder dataHolder,
                                       @NotNull TypeObjectDescriptor propertyType,
                                       @NotNull String propertyName) {
        TypeObject nested = propertyType.newInstance();
        if (YES.equals(propertyType.getShared())) {
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
