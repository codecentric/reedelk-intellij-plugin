package com.reedelk.plugin.component.deserializer;

import com.reedelk.module.descriptor.model.*;
import com.reedelk.plugin.converter.ValueConverter;
import com.reedelk.plugin.converter.ValueConverterProvider;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

public class ComponentDataHolderDeserializer {

    private static final ComponentDataHolderDeserializer INSTANCE = new ComponentDataHolderDeserializer();

    public static ComponentDataHolderDeserializer get() {
        return INSTANCE;
    }

    private ComponentDataHolderDeserializer() {
    }

    public void deserialize(@NotNull ComponentDataHolder componentData,
                            @NotNull PropertyDescriptor propertyDescriptor,
                            @NotNull JSONObject jsonObject) {

        String propertyName = propertyDescriptor.getName();
        TypeDescriptor propertyType = propertyDescriptor.getType();

        if (propertyType instanceof TypeObjectDescriptor) {
            TypeObjectDescriptor typeObjectDescriptor = (TypeObjectDescriptor) propertyType;
            TypeObjectDeserializer.get().deserialize(jsonObject, componentData, propertyName, typeObjectDescriptor);

        } else if (propertyType instanceof TypeMapDescriptor) {
            TypeMapDeserializer.get().deserialize(jsonObject, componentData, propertyName, (TypeMapDescriptor) propertyType);

        } else if (propertyType instanceof TypeListDescriptor) {
            TypeListDeserializer.get().deserialize(jsonObject, componentData, propertyName, (TypeListDescriptor) propertyType);

        } else {
            ValueConverter<?> converter = ValueConverterProvider.forType(propertyType);
            Object propertyValue = converter.from(propertyName, jsonObject);
            componentData.set(propertyName, propertyValue);
        }
    }
}
