package com.reedelk.plugin.component.serialization;

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
        process(componentData, parent, propertiesDescriptors);
    }

    public static JSONObject serialize(@NotNull TypeObjectDescriptor typeObjectDescriptor,
                                       @NotNull ComponentDataHolder dataHolder) {
        JSONObject jsonObject = JsonObjectFactory.newJSONObject();
        List<ComponentPropertyDescriptor> propertiesDescriptor = typeObjectDescriptor.getObjectProperties();
        process(dataHolder, jsonObject, propertiesDescriptor);
        return jsonObject;
    }

    private static void process(@NotNull ComponentDataHolder componentData,
                                @NotNull JSONObject parent, List<ComponentPropertyDescriptor> propertiesDescriptors) {
        propertiesDescriptors.forEach(propertyDescriptor -> {
            List<WhenDefinition> whenDefinitions = propertyDescriptor.getWhenDefinitions();
            if (whenDefinitions.isEmpty()) {
                process(componentData, parent, propertyDescriptor);
            } else {
                if (areAllSatisfied(whenDefinitions, componentData)) {
                    process(componentData, parent, propertyDescriptor);
                }
            }
        });
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

    private static void process(@NotNull ComponentDataHolder dataHolder,
                                @NotNull JSONObject jsonObject,
                                @NotNull ComponentPropertyDescriptor propertyDescriptor) {
        String propertyName = propertyDescriptor.getPropertyName();
        boolean existsPropertyValue = dataHolder.has(propertyName);
        if (!existsPropertyValue) return;

        Object data = dataHolder.get(propertyName);

        if (isTypeObject(data)) {
            processTypeObject(jsonObject, propertyDescriptor, propertyName, (TypeObject) data);
        } else {
            jsonObject.put(propertyName, data);
        }
    }

    private static void processTypeObject(@NotNull JSONObject jsonObject,
                                          @NotNull ComponentPropertyDescriptor propertyDescriptor,
                                          @NotNull String propertyName,
                                          @NotNull TypeObject data) {
        TypeObjectDescriptor propertyType = (TypeObjectDescriptor) propertyDescriptor.getPropertyType();
        if (Shareable.YES.equals(propertyType.getShareable())) {
            JSONObject refObject = JsonObjectFactory.newJSONObject();
            String ref = data.get(JsonParser.Component.configRef());
            JsonParser.Component.configRef(ref, refObject);
            jsonObject.put(propertyName, refObject);

        } else {
            JSONObject serialized = serialize(propertyType, data);
            serialized.put(JsonParser.Implementor.name(), propertyType.getTypeFullyQualifiedName());
            jsonObject.put(propertyName, serialized);
        }
    }

    private static boolean isTypeObject(Object data) {
        return data instanceof TypeObject;
    }
}
