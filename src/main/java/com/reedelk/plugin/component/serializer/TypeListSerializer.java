package com.reedelk.plugin.component.serializer;

import com.reedelk.module.descriptor.model.property.ListDescriptor;
import com.reedelk.module.descriptor.model.property.ObjectDescriptor;
import com.reedelk.module.descriptor.model.property.PropertyDescriptor;
import com.reedelk.plugin.commons.JsonObjectFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

class TypeListSerializer {

    private static final TypeListSerializer INSTANCE = new TypeListSerializer();

    static TypeListSerializer get() {
        return INSTANCE;
    }

    public void serialize(@NotNull PropertyDescriptor propertyDescriptor,
                          @NotNull JSONObject jsonObject,
                          @Nullable List<Object> list) {
        String propertyName = propertyDescriptor.getName();
        Stream.of(list)
                .filter(ExcludeEmptyLists)
                .forEach(theList -> {
                    List<Object> serialized = new ArrayList<>();
                    Objects.requireNonNull(theList).forEach((value) -> {
                        // The target value type of the list is user defined,
                        // therefore we need to serialize it it as well.
                        if (SerializerUtils.isTypeObject(value)) {

                            ListDescriptor type = propertyDescriptor.getType();
                            ObjectDescriptor objectDescriptor = (ObjectDescriptor) type.getValueType();
                            JSONObject serializedListItem = JsonObjectFactory.newJSONObject();

                            List<PropertyDescriptor> propertiesDescriptor = objectDescriptor.getObjectProperties();
                            TypeObjectSerializer.get()
                                    .serialize((ObjectDescriptor.TypeObject) value, serializedListItem, propertiesDescriptor);

                            serialized.add(serializedListItem);
                        } else {
                            serialized.add(value);
                        }
                    });
                    jsonObject.put(propertyName, serialized);
                });
    }

    // Empty Lists are excluded from serialization.
    private static final Predicate<Object> ExcludeEmptyLists = data -> {
        if (data instanceof List) {
            List<?> dataList = (List<?>) data;
            return !dataList.isEmpty();
        }
        return true;
    };
}
