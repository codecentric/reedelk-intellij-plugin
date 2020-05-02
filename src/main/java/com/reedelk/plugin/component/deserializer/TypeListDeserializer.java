package com.reedelk.plugin.component.deserializer;

import com.reedelk.module.descriptor.model.component.ComponentDataHolder;
import com.reedelk.module.descriptor.model.property.ListDescriptor;
import com.reedelk.module.descriptor.model.property.ObjectDescriptor;
import com.reedelk.module.descriptor.model.property.PropertyTypeDescriptor;
import com.reedelk.plugin.commons.TypeObjectFactory;
import com.reedelk.plugin.converter.ValueConverter;
import com.reedelk.plugin.converter.ValueConverterProvider;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TypeListDeserializer {

    private static final TypeListDeserializer INSTANCE = new TypeListDeserializer();

    static TypeListDeserializer get(){
        return INSTANCE;
    }

    void deserialize(@NotNull JSONObject componentJsonObject,
                     @NotNull ComponentDataHolder componentData,
                     @NotNull String propertyName,
                     @NotNull ListDescriptor propertyType) {

        PropertyTypeDescriptor valueType = propertyType.getValueType();
        if (valueType instanceof ObjectDescriptor) {
            // The user defined list looks like: List<OpenApiResponse> where OpenApiResponse is a user
            // defined type implementing 'implementor' interface.
            List<ObjectDescriptor.TypeObject> listOfObjects = new ArrayList<>();
            componentData.set(propertyName, listOfObjects);

            // Process each response type.
            ObjectDescriptor typeObjectDescriptor = (ObjectDescriptor) valueType;

            if (componentJsonObject.has(propertyName) && !componentJsonObject.isNull(propertyName)) {
                JSONArray jsonArray = componentJsonObject.getJSONArray(propertyName);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    ObjectDescriptor.TypeObject nestedObject = TypeObjectFactory.newInstance(typeObjectDescriptor);
                    typeObjectDescriptor
                            .getObjectProperties()
                            .forEach(typeDescriptor ->
                                    ComponentDataHolderDeserializer.get()
                                            .deserialize(nestedObject, typeDescriptor, jsonObject));
                    listOfObjects.add(nestedObject);
                }
            }

        } else {
            ValueConverter<?> converter = ValueConverterProvider.forType(propertyType);
            Object propertyValue = converter.from(propertyName, componentJsonObject);
            componentData.set(propertyName, propertyValue);
        }
    }
}
