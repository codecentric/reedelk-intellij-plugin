package com.reedelk.plugin.component.serializer;

import com.reedelk.plugin.commons.JsonObjectFactory;
import com.reedelk.plugin.component.domain.*;
import com.reedelk.runtime.commons.JsonParser;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.List;

import static com.reedelk.plugin.component.domain.TypeObjectDescriptor.TypeObject;

public class ComponentDataHolderSerializer {

    public static void serialize(@NotNull ComponentData componentData,
                                 @NotNull JSONObject parent) {
        List<ComponentPropertyDescriptor> propertiesDescriptors = componentData.getPropertiesDescriptors();
        serialize(componentData, parent, propertiesDescriptors);
    }

    public static void serialize(@NotNull TypeObjectDescriptor typeObjectDescriptor,
                                 @NotNull ComponentDataHolder dataHolder,
                                 @NotNull JSONObject jsonObject) {
        List<ComponentPropertyDescriptor> propertiesDescriptor = typeObjectDescriptor.getObjectProperties();
        serialize(dataHolder, jsonObject, propertiesDescriptor);
    }

    private static void serialize(@NotNull ComponentDataHolder componentData,
                                  @NotNull JSONObject parent,
                                  @NotNull List<ComponentPropertyDescriptor> propertiesDescriptors) {
        propertiesDescriptors.forEach(propertyDescriptor -> {
            List<WhenDefinition> whenDefinitions = propertyDescriptor.getWhenDefinitions();
            if (whenDefinitions.isEmpty()) {
                serialize(componentData, parent, propertyDescriptor);
            } else {
                // We just serialize if and only if all the when
                // conditions are satisfied.
                if (areAllSatisfied(whenDefinitions, componentData)) {
                    serialize(componentData, parent, propertyDescriptor);
                }
            }
        });
    }

    private static void serialize(@NotNull ComponentDataHolder dataHolder,
                                  @NotNull JSONObject jsonObject,
                                  @NotNull ComponentPropertyDescriptor propertyDescriptor) {
        String propertyName = propertyDescriptor.getPropertyName();
        if (dataHolder.has(propertyName)) {
            Object data = dataHolder.get(propertyName);
            if (isTypeObject(data)) {
                processTypeObject(jsonObject, propertyDescriptor, propertyName, (TypeObject) data);
            } else {
                jsonObject.put(propertyName, data);
            }
        }
    }

    private static void processTypeObject(@NotNull JSONObject jsonObject,
                                          @NotNull ComponentPropertyDescriptor propertyDescriptor,
                                          @NotNull String propertyName,
                                          @NotNull TypeObject data) {
        TypeObjectDescriptor propertyType = (TypeObjectDescriptor) propertyDescriptor.getPropertyType();
        if (Shared.YES.equals(propertyType.getShared())) {
            JSONObject refObject = JsonObjectFactory.newJSONObject();
            String ref = data.get(JsonParser.Component.configRef());
            JsonParser.Component.configRef(ref, refObject);
            jsonObject.put(propertyName, refObject);

        } else {
            JSONObject object = JsonObjectFactory.newJSONObject();
            object.put(JsonParser.Implementor.name(), propertyType.getTypeFullyQualifiedName());
            serialize(propertyType, data, object);
            jsonObject.put(propertyName, object);
        }
    }

    private static boolean isTypeObject(Object data) {
        return data instanceof TypeObject;
    }

    private static boolean areAllSatisfied(List<WhenDefinition> whenDefinitions, ComponentDataHolder dataHolder) {
        boolean satisfied = true;
        for (WhenDefinition definition : whenDefinitions) {
            String propertyName = definition.getPropertyName();
            String propertyValue = definition.getPropertyValue();
            Object value = dataHolder.get(propertyName);
            if (!(value != null && value.toString().equals(propertyValue))) {
                satisfied = false;
            }
        }
        return satisfied;
    }
}
