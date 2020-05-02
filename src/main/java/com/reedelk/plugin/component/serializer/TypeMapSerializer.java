package com.reedelk.plugin.component.serializer;

import com.reedelk.module.descriptor.model.property.MapDescriptor;
import com.reedelk.module.descriptor.model.property.ObjectDescriptor;
import com.reedelk.module.descriptor.model.property.PropertyDescriptor;
import com.reedelk.plugin.commons.JsonObjectFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

class TypeMapSerializer {

    private static final TypeMapSerializer INSTANCE = new TypeMapSerializer();

    static TypeMapSerializer get() {
        return INSTANCE;
    }

    public void serialize(@NotNull PropertyDescriptor propertyDescriptor,
                          @NotNull JSONObject jsonObject,
                          @Nullable Map<String, Object> map) {
        String propertyName = propertyDescriptor.getName();
        Stream.of(map)
                .filter(ExcludeEmptyMaps)
                .forEach(theMap -> {
                    Map<String, Object> serialized = new HashMap<>();
                    Objects.requireNonNull(theMap).forEach((key, value) -> {
                        // The target value type of the map is user defined,
                        // therefore we need to serialize it it as well.
                        if (SerializerUtils.isTypeObject(value)) {
                            MapDescriptor type = propertyDescriptor.getType();
                            ObjectDescriptor objectDescriptor = (ObjectDescriptor) type.getValueType();
                            JSONObject serializedMapValue = JsonObjectFactory.newJSONObject();

                            List<PropertyDescriptor> propertiesDescriptor = objectDescriptor.getObjectProperties();
                            TypeObjectSerializer.get().serialize((ObjectDescriptor.TypeObject) value, serializedMapValue, propertiesDescriptor);

                            serialized.put(key, serializedMapValue);
                        } else {
                            serialized.put(key, value);
                        }
                    });
                    jsonObject.put(propertyName, serialized);
                });
    }

    // Empty Maps are excluded from serialization.
    private static final Predicate<Object> ExcludeEmptyMaps = data -> {
        if (data instanceof Map) {
            Map<?, ?> dataMap = (Map<?, ?>) data;
            return !dataMap.isEmpty();
        }
        return true;
    };
}
