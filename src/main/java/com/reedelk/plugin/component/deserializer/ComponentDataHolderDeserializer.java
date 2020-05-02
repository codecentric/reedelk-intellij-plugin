package com.reedelk.plugin.component.deserializer;

import com.reedelk.module.descriptor.model.component.ComponentDataHolder;
import com.reedelk.module.descriptor.model.property.*;
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
        PropertyTypeDescriptor propertyType = propertyDescriptor.getType();

        if (propertyType instanceof ObjectDescriptor) {
            ObjectDescriptor typeObjectDescriptor = (ObjectDescriptor) propertyType;
            TypeObjectDeserializer.get().deserialize(jsonObject, componentData, propertyName, typeObjectDescriptor);

        } else if (propertyType instanceof MapDescriptor) {
            TypeMapDeserializer.get().deserialize(jsonObject, componentData, propertyName, (MapDescriptor) propertyType);

        } else if (propertyType instanceof ListDescriptor) {
            TypeListDeserializer.get().deserialize(jsonObject, componentData, propertyName, (ListDescriptor) propertyType);

        } else {
            ValueConverter<?> converter = ValueConverterProvider.forType(propertyType);
            Object propertyValue = converter.from(propertyName, jsonObject);
            componentData.set(propertyName, propertyValue);
        }
    }
}
