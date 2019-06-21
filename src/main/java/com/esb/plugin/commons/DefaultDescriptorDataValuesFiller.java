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
                TypeObjectDescriptor typeObjectDescriptor = (TypeObjectDescriptor) propertyType;
                if (typeObjectDescriptor.isShareable()) {
                    TypeObject nested = typeObjectDescriptor.newInstance();
                    nested.set(Component.configRef(), TypeObject.DEFAULT_CONFIG_REF);
                    dataHolder.set(propertyName, nested);
                } else {
                    fillTypeObjectDescriptor(dataHolder, propertyName, typeObjectDescriptor);
                }
            } else {
                Object defaultValue = descriptor.getDefaultValue();
                dataHolder.set(propertyName, defaultValue);
            }
        });
    }

    private static void fillTypeObjectDescriptor(ComponentDataHolder componentData, String propertyName, TypeObjectDescriptor propertyType) {
        TypeObject typeObject = propertyType.newInstance();
        componentData.set(propertyName, typeObject);
        propertyType
                .getObjectProperties()
                .forEach(nestedDescriptor ->
                        typeObject.set(nestedDescriptor.getPropertyName(), nestedDescriptor.getDefaultValue()));
    }
}
