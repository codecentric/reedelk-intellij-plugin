package com.reedelk.plugin.component.deserializer;

import com.reedelk.plugin.commons.TypeObjectFactory;
import com.reedelk.plugin.component.domain.ComponentDataHolder;
import com.reedelk.plugin.component.domain.ComponentPropertyDescriptor;
import com.reedelk.plugin.component.domain.TypeDescriptor;
import com.reedelk.plugin.component.domain.TypeObjectDescriptor;
import com.reedelk.plugin.converter.ValueConverter;
import com.reedelk.plugin.converter.ValueConverterProvider;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import static com.reedelk.plugin.component.domain.Shared.YES;
import static com.reedelk.plugin.component.domain.TypeObjectDescriptor.TypeObject;
import static com.reedelk.runtime.commons.JsonParser.Component;

public class ComponentDataHolderDeserializer {

    private ComponentDataHolderDeserializer() {
    }

    public static void deserialize(@NotNull JSONObject componentJsonObject,
                                   @NotNull ComponentDataHolder componentData,
                                   @NotNull ComponentPropertyDescriptor propertyDescriptor) {
        TypeDescriptor propertyType = propertyDescriptor.getPropertyType();

        if (propertyType instanceof TypeObjectDescriptor) {
            deserializeTypeObject(componentJsonObject, componentData, propertyDescriptor, (TypeObjectDescriptor) propertyType);
        } else {
            ValueConverter<?> converter = ValueConverterProvider.forType(propertyType);
            Object propertyValue = converter.from(propertyDescriptor.getPropertyName(), componentJsonObject);
            componentData.set(propertyDescriptor.getPropertyName(), propertyValue);
        }
    }

    private static void deserializeTypeObject(@NotNull JSONObject componentJsonObject,
                                              @NotNull ComponentDataHolder componentData,
                                              @NotNull ComponentPropertyDescriptor descriptor,
                                              @NotNull TypeObjectDescriptor propertyType) {

        TypeObject nestedObject = TypeObjectFactory.newInstance(propertyType);

        if (componentJsonObject.has(descriptor.getPropertyName()) &&
                !componentJsonObject.isNull(descriptor.getPropertyName())) {

            JSONObject nestedJsonObject = componentJsonObject.getJSONObject(descriptor.getPropertyName());

            // If the property is present in the JSON but it is an empty object we
            // still fill up instances of type object for object properties recursively.
            // This is needed, to enable the UI to fill up the values in the
            // Type Object properties when the user edits a value.
            // This happens for instance when we have a JSON object like the following:
            // "configuration": {}
            if (nestedJsonObject.isEmpty()) {
                addEmptyInstancesForTypeObject(componentData, descriptor);

            } else if (YES.equals(propertyType.getShared())) {
                // The config is shareable, therefore we just set the
                // reference value pointing to the shared config.
                if (nestedJsonObject.has(Component.ref())) {
                    String reference = Component.ref(nestedJsonObject);
                    nestedObject.set(Component.ref(), reference);
                    componentData.set(descriptor.getPropertyName(), nestedObject);
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
                componentData.set(descriptor.getPropertyName(), nestedObject);
            }

        } else {
            // If the property is not present in the JSON we still fill up
            // Instances of type object for object properties recursively.
            // This is needed, to enable the UI to fill up the values in the
            // Type Object properties when the user edits a value.
            addEmptyInstancesForTypeObject(componentData, descriptor);
        }
    }

    private static void addEmptyInstancesForTypeObject(ComponentDataHolder dataHolder, ComponentPropertyDescriptor descriptor) {
        if (descriptor.getPropertyType() instanceof TypeObjectDescriptor) {
            TypeObjectDescriptor propertyObjectType = descriptor.getPropertyType();
            TypeObject typeObject = TypeObjectFactory.newInstance(propertyObjectType);

            dataHolder.set(descriptor.getPropertyName(), typeObject);
            // From now on, the subtree contains null objects.
            propertyObjectType.getObjectProperties()
                    .forEach(propertyDescriptor ->
                            addEmptyInstancesForTypeObject(typeObject, propertyDescriptor));
        }
    }
}
