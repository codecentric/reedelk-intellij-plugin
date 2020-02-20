package com.reedelk.plugin.component.deserializer;

import com.reedelk.module.descriptor.model.*;
import com.reedelk.plugin.commons.TypeObjectFactory;
import com.reedelk.plugin.converter.ValueConverter;
import com.reedelk.plugin.converter.ValueConverterProvider;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import static com.reedelk.module.descriptor.model.TypeObjectDescriptor.TypeObject;
import static com.reedelk.runtime.commons.JsonParser.Component;

public class ComponentDataHolderDeserializer {

    private ComponentDataHolderDeserializer() {
    }

    public static void deserialize(@NotNull JSONObject componentJsonObject,
                                   @NotNull ComponentDataHolder componentData,
                                   @NotNull PropertyDescriptor propertyDescriptor) {
        TypeDescriptor propertyType = propertyDescriptor.getType();

        if (propertyType instanceof TypeObjectDescriptor) {
            deserializeTypeObject(componentJsonObject, componentData, propertyDescriptor, (TypeObjectDescriptor) propertyType);
        } else {
            ValueConverter<?> converter = ValueConverterProvider.forType(propertyType);
            Object propertyValue = converter.from(propertyDescriptor.getName(), componentJsonObject);
            componentData.set(propertyDescriptor.getName(), propertyValue);
        }
    }

    private static void deserializeTypeObject(@NotNull JSONObject componentJsonObject,
                                              @NotNull ComponentDataHolder componentData,
                                              @NotNull PropertyDescriptor descriptor,
                                              @NotNull TypeObjectDescriptor propertyType) {

        TypeObject nestedObject = TypeObjectFactory.newInstance(propertyType);

        if (componentJsonObject.has(descriptor.getName()) &&
                !componentJsonObject.isNull(descriptor.getName())) {

            JSONObject nestedJsonObject = componentJsonObject.getJSONObject(descriptor.getName());

            // If the property is present in the JSON but it is an empty object we
            // still fill up instances of type object for object properties recursively.
            // This is needed, to enable the UI to fill up the values in the
            // Type Object properties when the user edits a value.
            // This happens for instance when we have a JSON object like the following:
            // "configuration": {}
            if (nestedJsonObject.isEmpty()) {
                addEmptyInstancesForTypeObject(componentData, descriptor);

            } else if (Shared.YES.equals(propertyType.getShared())) {
                // The config is shareable, therefore we just set the
                // reference value pointing to the shared config.
                if (nestedJsonObject.has(Component.ref())) {
                    String reference = Component.ref(nestedJsonObject);
                    nestedObject.set(Component.ref(), reference);
                    componentData.set(descriptor.getName(), nestedObject);
                } else {
                    // The nested JSON object does not have a component "ref" property inside it.
                    // We still must add empty instances for the type object.
                    addEmptyInstancesForTypeObject(componentData, descriptor);
                }
            } else {
                // The config is not shareable, hence we deserialize the object right away.
                propertyType.getObjectProperties()
                        .forEach(typeDescriptor ->
                                deserialize(nestedJsonObject, nestedObject, typeDescriptor));
                componentData.set(descriptor.getName(), nestedObject);
            }

        } else {
            // If the property is not present in the JSON we still fill up
            // Instances of type object for object properties recursively.
            // This is needed, to enable the UI to fill up the values in the
            // Type Object properties when the user edits a value.
            addEmptyInstancesForTypeObject(componentData, descriptor);
        }
    }

    private static void addEmptyInstancesForTypeObject(ComponentDataHolder dataHolder, PropertyDescriptor descriptor) {
        if (descriptor.getType() instanceof TypeObjectDescriptor) {
            TypeObjectDescriptor propertyObjectType = descriptor.getType();
            TypeObject typeObject = TypeObjectFactory.newInstance(propertyObjectType);

            dataHolder.set(descriptor.getName(), typeObject);
            // From now on, the subtree contains null objects.
            propertyObjectType.getObjectProperties()
                    .forEach(propertyDescriptor ->
                            addEmptyInstancesForTypeObject(typeObject, propertyDescriptor));
        }
    }
}
