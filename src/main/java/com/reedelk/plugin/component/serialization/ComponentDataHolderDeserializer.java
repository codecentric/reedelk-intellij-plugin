package com.reedelk.plugin.component.serialization;

import com.reedelk.plugin.component.domain.ComponentDataHolder;
import com.reedelk.plugin.component.domain.ComponentPropertyDescriptor;
import com.reedelk.plugin.component.domain.TypeDescriptor;
import com.reedelk.plugin.component.domain.TypeObjectDescriptor;
import com.reedelk.plugin.converter.ValueConverterFactory;
import com.reedelk.runtime.commons.JsonParser;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

public class ComponentDataHolderDeserializer {

    public static void deserialize(JSONObject componentJsonObject, ComponentDataHolder componentData, ComponentPropertyDescriptor descriptor) {
        TypeDescriptor propertyType = descriptor.getPropertyType();

        // TODO: This is wrong. Should just deserialize all the  properties from the definition,
        //  TODO:  if there is no match in the json, then nothing happens...
        // Also if the property is a typed object, it should add the object empty  in the container
        if (propertyType instanceof TypeObjectDescriptor) {
            deserializeTypeObject(componentJsonObject, componentData, descriptor, (TypeObjectDescriptor) propertyType);

        } else {
            Object propertyValue = ValueConverterFactory
                    .forType(propertyType)
                    .from(descriptor.getPropertyName(), componentJsonObject);
            componentData.set(descriptor.getPropertyName(), propertyValue);
        }
    }

    private static void deserializeTypeObject(@NotNull JSONObject componentJsonObject,
                                              @NotNull ComponentDataHolder componentData,
                                              @NotNull ComponentPropertyDescriptor descriptor,
                                              @NotNull TypeObjectDescriptor propertyType) {


        boolean shareable = propertyType.isShareable();

        JSONObject nestedJsonObject = componentJsonObject.getJSONObject(descriptor.getPropertyName());

        TypeObjectDescriptor.TypeObject nestedObject = propertyType.newInstance();

        if (shareable) {
            // The object must contain a reference
            if (nestedJsonObject.has(JsonParser.Component.configRef())) {
                String configRef = JsonParser.Component.configRef(nestedJsonObject);
                nestedObject.set(JsonParser.Component.configRef(), configRef);
                componentData.set(descriptor.getPropertyName(), nestedObject);
            } else {
                throw new IllegalStateException("Expected config ref for @Shareable configuration");
            }

        } else {
            // The config is not shareable, hence we deserialize the object right away.
            propertyType.getObjectProperties()
                    .forEach(typeDescriptor ->
                            deserialize(nestedJsonObject, nestedObject, typeDescriptor));
            componentData.set(descriptor.getPropertyName(), nestedObject);
        }
    }

}
