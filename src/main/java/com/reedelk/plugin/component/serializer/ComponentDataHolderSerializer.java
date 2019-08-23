package com.reedelk.plugin.component.serializer;

import com.reedelk.plugin.commons.JsonObjectFactory;
import com.reedelk.plugin.component.domain.*;
import com.reedelk.runtime.commons.JsonParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Stream;

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
                putData(jsonObject, propertyName, data);
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
            putData(jsonObject, propertyName, refObject);

        } else {
            JSONObject object = JsonObjectFactory.newJSONObject();
            object.put(JsonParser.Implementor.name(), propertyType.getTypeFullyQualifiedName());
            serialize(propertyType, data, object);
            putData(jsonObject, propertyName, object);
        }
    }

    private static void putData(@NotNull JSONObject jsonObject,
                                @NotNull String propertyName,
                                @Nullable Object data) {
        Stream.of(data).filter(ExcludeEmptyMaps)
                .forEach(filteredData -> jsonObject.put(propertyName, filteredData));
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

    /**
     * A predicate which return false if the given
     * object has type Map and it is empty.
     */
    private static final Predicate<Object> ExcludeEmptyMaps = data -> {
        if (data instanceof Map) {
            Map dataMap = (Map) data;
            return !dataMap.isEmpty();
        }
        return true;
    };
}
