package com.reedelk.plugin.component.serializer;

import com.reedelk.module.descriptor.model.component.ComponentDataHolder;
import com.reedelk.module.descriptor.model.property.ObjectDescriptor;
import com.reedelk.module.descriptor.model.property.PropertyDescriptor;
import com.reedelk.module.descriptor.model.property.Shared;
import com.reedelk.module.descriptor.model.property.WhenDescriptor;
import com.reedelk.plugin.commons.AtLeastOneWhenConditionIsTrue;
import com.reedelk.plugin.commons.JsonObjectFactory;
import com.reedelk.runtime.api.commons.ScriptUtils;
import com.reedelk.runtime.api.commons.StringUtils;
import com.reedelk.runtime.commons.JsonParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Stream;

class TypeObjectSerializer {

    private static final TypeObjectSerializer INSTANCE = new TypeObjectSerializer();

    static TypeObjectSerializer get() {
        return INSTANCE;
    }

    public void serialize(@NotNull ComponentDataHolder componentData,
                          @NotNull JSONObject parent,
                          @NotNull List<PropertyDescriptor> propertiesDescriptors) {
        propertiesDescriptors.forEach(propertyDescriptor -> {
            List<WhenDescriptor> whens = propertyDescriptor.getWhens();
            if (whens.isEmpty()) {
                // If there are no when conditions, we serialize the value.
                serialize(componentData, parent, propertyDescriptor);
            } else {
                // We just serialize the property if and only if all the when conditions are satisfied.
                if (AtLeastOneWhenConditionIsTrue.of(whens, componentData::get)) {
                    serialize(componentData, parent, propertyDescriptor);
                }
            }
        });
    }

    private void serialize(@NotNull PropertyDescriptor propertyDescriptor,
                           @NotNull JSONObject jsonObject,
                           @NotNull ObjectDescriptor.TypeObject data) {
        ObjectDescriptor propertyType = propertyDescriptor.getType();
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

            List<PropertyDescriptor> propertiesDescriptor = propertyType.getObjectProperties();
            TypeObjectSerializer.get().serialize(data, object, propertiesDescriptor);

            putData(propertyDescriptor, jsonObject, object);
        }
    }

    @SuppressWarnings("unchecked")
    private void serialize(@NotNull ComponentDataHolder dataHolder,
                           @NotNull JSONObject jsonObject,
                           @NotNull PropertyDescriptor propertyDescriptor) {
        String propertyName = propertyDescriptor.getName();
        if (dataHolder.has(propertyName)) {
            Object data = dataHolder.get(propertyName);
            if (SerializerUtils.isTypeObject(data)) {
                serialize(propertyDescriptor, jsonObject, (ObjectDescriptor.TypeObject) data);
            } else if (isTypeMap(data)) {
                TypeMapSerializer.get().serialize(propertyDescriptor, jsonObject, (Map<String, Object>) data);
            } else if (isTypeList(data)) {
                TypeListSerializer.get().serialize(propertyDescriptor, jsonObject, (List<Object>) data);
            } else {
                putData(propertyDescriptor, jsonObject, data);
            }
        }
    }

    private void putData(@NotNull PropertyDescriptor propertyDescriptor,
                         @NotNull JSONObject jsonObject,
                         @Nullable Object data) {
        String propertyName = propertyDescriptor.getName();
        Stream.of(data)
                .filter(ExcludeEmptyObjects)
                .filter(ExcludeBooleanFalse)
                .filter(ExcludeEmptyStringsAndDynamicValues)
                .forEach(filteredData -> jsonObject.put(propertyName, filteredData));
    }

    private boolean isTypeMap(Object data) {
        return data instanceof Map;
    }

    private boolean isTypeList(Object data) {
        return data instanceof List;
    }

    // Boolean values with 'false' are excluded from serialization.
    private static final Predicate<Object> ExcludeBooleanFalse = data -> {
        if (data instanceof Boolean) {
            return (Boolean) data;
        }
        return true;
    };

    // Empty Objects are excluded from serialization.
    private static final Predicate<Object> ExcludeEmptyObjects = data -> {
        if (data instanceof JSONObject) {
            return !((JSONObject) data).isEmpty();
        }
        return true;
    };

    // Empty Strings and Empty Scripts are excluded from serialization.
    private static final Predicate<Object> ExcludeEmptyStringsAndDynamicValues = data -> {
        if (data instanceof String) {
            String dataAsString = (String) data;
            if (ScriptUtils.isScript(dataAsString)) {
                return ScriptUtils.isNotBlank(dataAsString);
            } else {
                return dataAsString.length() != 0;
            }
        }
        return true;
    };
}
