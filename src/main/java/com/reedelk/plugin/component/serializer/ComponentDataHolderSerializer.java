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
                // We just serialize if and only if all
                // the when conditions are satisfied.
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
                processTypeObject(propertyDescriptor, jsonObject, (TypeObject) data);
            } else {
                putData(propertyDescriptor, jsonObject, data);
            }
        }
    }

    private static void processTypeObject(@NotNull ComponentPropertyDescriptor propertyDescriptor,
                                          @NotNull JSONObject jsonObject,
                                          @NotNull TypeObject data) {
        TypeObjectDescriptor propertyType = propertyDescriptor.getPropertyType();
        if (Shared.YES.equals(propertyType.getShared())) {
            JSONObject refObject = JsonObjectFactory.newJSONObject();
            String ref = data.get(JsonParser.Component.configRef());
            JsonParser.Component.configRef(ref, refObject);
            putData(propertyDescriptor, jsonObject, refObject);
        } else {
            // We DO NOT have to put the implementor name if the object is not shared.
            JSONObject object = JsonObjectFactory.newJSONObject();
            serialize(propertyType, data, object);
            putData(propertyDescriptor, jsonObject, object);
        }
    }

    private static void putData(@NotNull ComponentPropertyDescriptor propertyDescriptor,
                                @NotNull JSONObject jsonObject,
                                @Nullable Object data) {
        String propertyName = propertyDescriptor.getPropertyName();
        Transient isTransient = propertyDescriptor.getIsTransient();

        // We only serialize if the property is NOT transient.
        // If it is transient it should not be put in the final JSON.
        if (Transient.NO.equals(isTransient)) {
            Stream.of(data)
                    .filter(ExcludeEmptyMaps)
                    .filter(ExcludeEmptyObjects)
                    .forEach(filteredData -> jsonObject.put(propertyName, filteredData));
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

    // Empty Maps are excluded from serialization
    private static final Predicate<Object> ExcludeEmptyMaps = data -> {
        if (data instanceof Map) {
            Map dataMap = (Map) data;
            return !dataMap.isEmpty();
        }
        return true;
    };

    // Empty Objects are excluded from serialization
    private static final Predicate<Object> ExcludeEmptyObjects = data -> {
        if (data instanceof JSONObject) {
            return !((JSONObject) data).isEmpty();
        }
        return true;
    };
}
