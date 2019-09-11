package com.reedelk.plugin.component.deserializer;

import com.reedelk.plugin.component.domain.ComponentDataHolder;
import com.reedelk.plugin.component.domain.ComponentPropertyDescriptor;
import com.reedelk.plugin.component.domain.TypeDescriptor;
import com.reedelk.plugin.component.domain.TypeObjectDescriptor;
import com.reedelk.plugin.converter.ValueConverterFactory;
import com.reedelk.runtime.commons.JsonParser;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import static com.reedelk.plugin.component.domain.Shared.YES;

public class ComponentDataHolderDeserializer {

    public static void deserialize(@NotNull JSONObject componentJsonObject,
                                   @NotNull ComponentDataHolder componentData,
                                   @NotNull ComponentPropertyDescriptor propertyDescriptor) {
        TypeDescriptor propertyType = propertyDescriptor.getPropertyType();

        if (propertyType instanceof TypeObjectDescriptor) {
            deserializeTypeObject(componentJsonObject, componentData, propertyDescriptor, (TypeObjectDescriptor) propertyType);
        } else {
            Object propertyValue = ValueConverterFactory
                    .forType(propertyType)
                    .from(propertyDescriptor.getPropertyName(), componentJsonObject);
            componentData.set(propertyDescriptor.getPropertyName(), propertyValue);
        }
    }

    private static void deserializeTypeObject(@NotNull JSONObject componentJsonObject,
                                              @NotNull ComponentDataHolder componentData,
                                              @NotNull ComponentPropertyDescriptor descriptor,
                                              @NotNull TypeObjectDescriptor propertyType) {

        TypeObjectDescriptor.TypeObject nestedObject = propertyType.newInstance();

        if (componentJsonObject.has(descriptor.getPropertyName()) &&
                !componentJsonObject.isNull(descriptor.getPropertyName())) {

            JSONObject nestedJsonObject = componentJsonObject.getJSONObject(descriptor.getPropertyName());

            if (YES.equals(propertyType.getShared())) {
                // The config is shareable, therefore we just set the reference value
                // pointing to the shared config.
                if (nestedJsonObject.has(JsonParser.Component.configRef())) {
                    String configRef = JsonParser.Component.configRef(nestedJsonObject);
                    nestedObject.set(JsonParser.Component.configRef(), configRef);
                    componentData.set(descriptor.getPropertyName(), nestedObject);
                } else {
                    throw new IllegalStateException("Expected config ref for @Shared configuration");
                }

            } else {
                // The config is not shareable, hence we deserialize the object right away.
                propertyType.getObjectProperties()
                        .forEach(typeDescriptor ->
                                deserialize(nestedJsonObject, nestedObject, typeDescriptor));
                componentData.set(descriptor.getPropertyName(), nestedObject);
            }

        } else {
            // If the property is not present in the JSON we still fill up
            // Instances of type object for object properties recursively.
            // This is needed, to enable the UI to fill up the values in the
            // Type Object properties when the user edits a value.
            addEmptyObjectsInstancesForTypeObject(componentData, descriptor);
        }
    }

    private static void addEmptyObjectsInstancesForTypeObject(ComponentDataHolder dataHolder, ComponentPropertyDescriptor descriptor) {
        if (descriptor.getPropertyType() instanceof TypeObjectDescriptor) {
            TypeObjectDescriptor propertyObjectType = descriptor.getPropertyType();
            TypeObjectDescriptor.TypeObject typeObject = propertyObjectType.newInstance();
            dataHolder.set(descriptor.getPropertyName(), typeObject);
            // From now on, the subtree contains null objects.
            propertyObjectType.getObjectProperties()
                    .forEach(propertyDescriptor ->
                            addEmptyObjectsInstancesForTypeObject(typeObject, propertyDescriptor));
        }
    }
}
