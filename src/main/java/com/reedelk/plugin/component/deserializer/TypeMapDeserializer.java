package com.reedelk.plugin.component.deserializer;

import com.reedelk.module.descriptor.model.ComponentDataHolder;
import com.reedelk.module.descriptor.model.TypeDescriptor;
import com.reedelk.module.descriptor.model.TypeMapDescriptor;
import com.reedelk.module.descriptor.model.TypeObjectDescriptor;
import com.reedelk.plugin.converter.ValueConverter;
import com.reedelk.plugin.converter.ValueConverterProvider;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class TypeMapDeserializer {

    private static final TypeMapDeserializer INSTANCE = new TypeMapDeserializer();

    static TypeMapDeserializer get(){
        return INSTANCE;
    }

    void deserialize(@NotNull JSONObject componentJsonObject,
                     @NotNull ComponentDataHolder componentData,
                     @NotNull String propertyName,
                     @NotNull TypeMapDescriptor propertyType) {

        // The deserialization depends on the value type of the map.
        // The value type could be a complex or a primitive type.
        TypeDescriptor valueType = propertyType.getValueType();
        if (valueType instanceof TypeObjectDescriptor) {
            // The user defined map looks like: Map<String, OpenApiResponse> where OpenApiResponse is a user
            // defined type implementing 'implementor' interface.
            Map<String, TypeObjectDescriptor.TypeObject> keyAndValues = new HashMap<>();
            componentData.set(propertyName, keyAndValues);

            // Process each response type.
            TypeObjectDescriptor typeObjectDescriptor = (TypeObjectDescriptor) valueType;

            if (componentJsonObject.has(propertyName) && !componentJsonObject.isNull(propertyName)) {
                JSONObject jsonObject = componentJsonObject.getJSONObject(propertyName);
                jsonObject.keySet().forEach(key -> {
                    MapComponentDataHolder dataHolder = new MapComponentDataHolder(keyAndValues);
                    TypeObjectDeserializer.get().deserialize(jsonObject, dataHolder, key, typeObjectDescriptor);
                });
            }

        } else {
            ValueConverter<?> converter = ValueConverterProvider.forType(propertyType);
            Object propertyValue = converter.from(propertyName, componentJsonObject);
            componentData.set(propertyName, propertyValue);
        }
    }

    static class MapComponentDataHolder implements ComponentDataHolder {

        private final Map<String, TypeObjectDescriptor.TypeObject> delegate;

        public MapComponentDataHolder(Map<String, TypeObjectDescriptor.TypeObject> delegate) {
            this.delegate = delegate;
        }


        @Override
        public List<String> keys() {
            return new ArrayList<>(delegate.keySet());
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> T get(String key) {
            return (T) delegate.get(key);
        }

        @Override
        public void set(String key, Object value) {
            delegate.put(key, (TypeObjectDescriptor.TypeObject) value);
        }

        @Override
        public boolean has(String key) {
            return delegate.containsKey(key);
        }
    }
}
