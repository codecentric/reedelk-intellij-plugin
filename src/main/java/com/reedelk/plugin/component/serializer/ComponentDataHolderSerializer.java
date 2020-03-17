package com.reedelk.plugin.component.serializer;

import com.reedelk.module.descriptor.model.*;
import com.reedelk.plugin.commons.AtLeastOneWhenConditionIsTrue;
import com.reedelk.plugin.commons.JsonObjectFactory;
import com.reedelk.plugin.component.ComponentData;
import com.reedelk.runtime.api.commons.StringUtils;
import com.reedelk.runtime.commons.JsonParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.reedelk.module.descriptor.model.TypeObjectDescriptor.TypeObject;

public class ComponentDataHolderSerializer {

    private ComponentDataHolderSerializer() {
    }

    public static void serialize(@NotNull ComponentData componentData,
                                 @NotNull JSONObject parent) {
        List<PropertyDescriptor> propertiesDescriptors = componentData.getPropertiesDescriptors();
        serialize(componentData, parent, propertiesDescriptors);
    }

    public static void serialize(@NotNull TypeObjectDescriptor typeObjectDescriptor,
                                 @NotNull ComponentDataHolder dataHolder,
                                 @NotNull JSONObject jsonObject) {
        List<PropertyDescriptor> propertiesDescriptor = typeObjectDescriptor.getObjectProperties();
        serialize(dataHolder, jsonObject, propertiesDescriptor);
    }

    private static void serialize(@NotNull ComponentDataHolder componentData,
                                  @NotNull JSONObject parent,
                                  @NotNull List<PropertyDescriptor> propertiesDescriptors) {
        propertiesDescriptors.forEach(propertyDescriptor -> {
            List<WhenDescriptor> whens = propertyDescriptor.getWhens();
            if (whens.isEmpty()) {
                // If there are no when conditions, we serialize the value.
                serialize(componentData, parent, propertyDescriptor);
            } else {
                // We just serialize the property if and only if
                // all the when conditions are satisfied.
                if (AtLeastOneWhenConditionIsTrue.of(whens, componentData::get)) {
                    serialize(componentData, parent, propertyDescriptor);
                }
            }
        });
    }

    private static void serialize(@NotNull ComponentDataHolder dataHolder,
                                  @NotNull JSONObject jsonObject,
                                  @NotNull PropertyDescriptor propertyDescriptor) {
        String propertyName = propertyDescriptor.getName();
        if (dataHolder.has(propertyName)) {
            Object data = dataHolder.get(propertyName);
            if (isTypeObject(data)) {
                processTypeObject(propertyDescriptor, jsonObject, (TypeObject) data);
            } else {
                // TODO: If type map and the value is type object, then problem
                putData(propertyDescriptor, jsonObject, data);
            }
        }
    }

    private static void processTypeObject(@NotNull PropertyDescriptor propertyDescriptor,
                                          @NotNull JSONObject jsonObject,
                                          @NotNull TypeObject data) {
        TypeObjectDescriptor propertyType = propertyDescriptor.getType();
        if (Shared.YES.equals(propertyType.getShared())) {
            String ref = data.get(JsonParser.Component.ref());
            // An object reference is ONLY serialized when it is present and it is NOT blank.
            // e.g. the following reference '"ref": ""' it is not serialized.
            // e.g. the following reference '"ref": "aabbff11233"' it is serialized.
            if (StringUtils.isNotBlank(ref)) {
                JSONObject refObject = JsonObjectFactory.newJSONObject();
                JsonParser.Component.ref(ref, refObject);
                putData(propertyDescriptor, jsonObject, refObject);
            }
        } else {
            // We DO NOT have to put the implementor name if the object is not shared.
            JSONObject object = JsonObjectFactory.newJSONObject();
            serialize(propertyType, data, object);
            putData(propertyDescriptor, jsonObject, object);
        }
    }

    private static void putData(@NotNull PropertyDescriptor propertyDescriptor,
                                @NotNull JSONObject jsonObject,
                                @Nullable Object data) {
        String propertyName = propertyDescriptor.getName();
        Stream.of(data)
                .filter(ExcludeEmptyMaps)
                .filter(ExcludeEmptyObjects)
                .filter(ExcludeBooleanFalse)
                .forEach(filteredData -> {
                    if (filteredData instanceof Map) {
                        Map<String,Object> test = new HashMap<>();
                        ((Map<String,Object>)filteredData).forEach(new BiConsumer<String, Object>() {
                            @Override
                            public void accept(String key, Object value) {
                                if (isTypeObject(value)) {
                                    TypeMapDescriptor type = propertyDescriptor.getType();
                                    TypeObjectDescriptor objectDescriptor = (TypeObjectDescriptor) type.getValueType();
                                    JSONObject refObject = JsonObjectFactory.newJSONObject();
                                    serialize(objectDescriptor, (TypeObject) value, refObject);
                                    test.put(key, refObject);
                                } else {
                                    test.put(key, value);
                                }
                            }
                        });

                        jsonObject.put(propertyName, test);

                    } else {
                        jsonObject.put(propertyName, filteredData);
                    }
                });
    }

    /**
     * Empty Maps are excluded from serialization.
     */
    private static final Predicate<Object> ExcludeEmptyMaps = data -> {
        if (data instanceof Map) {
            Map<?,?> dataMap = (Map<?,?>) data;
            return !dataMap.isEmpty();
        }
        return true;
    };

    /**
     * Boolean values with 'false' are excluded from serialization.
     */
    private static final Predicate<Object> ExcludeBooleanFalse = data -> {
        if (data instanceof Boolean) {
            return (Boolean) data;
        }
        return true;
    };

    /**
     * Empty Objects are excluded from serialization.
     */
    private static final Predicate<Object> ExcludeEmptyObjects = data -> {
        if (data instanceof JSONObject) {
            return !((JSONObject) data).isEmpty();
        }
        return true;
    };

    private static boolean isTypeObject(Object data) {
        return data instanceof TypeObject;
    }
}
