package de.codecentric.reedelk.plugin.commons;

import de.codecentric.reedelk.module.descriptor.model.component.ComponentDataHolder;
import de.codecentric.reedelk.module.descriptor.model.property.ObjectDescriptor;
import de.codecentric.reedelk.module.descriptor.model.property.PropertyDescriptor;
import de.codecentric.reedelk.module.descriptor.model.property.PropertyTypeDescriptor;
import de.codecentric.reedelk.module.descriptor.model.property.Shared;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static de.codecentric.reedelk.runtime.commons.JsonParser.Component;

public class InitValuesFiller {

    private InitValuesFiller() {
    }

    public static void fill(@NotNull ComponentDataHolder dataHolder,
                            @NotNull List<PropertyDescriptor> propertyDescriptors) {
        propertyDescriptors.forEach(descriptor -> {
            String propertyName = descriptor.getName();
            PropertyTypeDescriptor propertyType = descriptor.getType();

            if (propertyType instanceof ObjectDescriptor) {
                fillTypeObject(dataHolder, (ObjectDescriptor) propertyType, propertyName);
            } else {
                Object initValue = InitPropertyValue.of(descriptor);
                dataHolder.set(propertyName, initValue);
            }
        });
    }

    private static void fillTypeObject(@NotNull ComponentDataHolder dataHolder,
                                       @NotNull ObjectDescriptor propertyType,
                                       @NotNull String propertyName) {
        ObjectDescriptor.TypeObject nested = TypeObjectFactory.newInstance(propertyType);
        if (Shared.YES.equals(propertyType.getShared())) {
            // If the property is shareable, we initialize it with default config ref
            nested.set(Component.ref(), ObjectDescriptor.TypeObject.DEFAULT_CONFIG_REF);
            dataHolder.set(propertyName, nested);
        } else {
            // Recursively fill the content of this object
            dataHolder.set(propertyName, nested);
            fill(nested, propertyType.getObjectProperties());
        }
    }
}
