package com.reedelk.plugin.component.deserializer;

import com.reedelk.module.descriptor.model.ComponentDataHolder;
import com.reedelk.module.descriptor.model.TypeDescriptor;
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

    void deserialize(@NotNull JSONObject componentJsonObject, @NotNull ComponentDataHolder componentData, String propertyName, TypeDescriptor propertyType, TypeDescriptor valueType) {
        if (valueType instanceof TypeObjectDescriptor) {
            // The map is like: Map<String, OpenApiResponse>, there OpenApiResponse is a user defined type (Implementor).
            Map<String, TypeObjectDescriptor.TypeObject> keyAndValues = new HashMap<>();
            componentData.set(propertyName, keyAndValues);

            // Process each response.
            TypeObjectDescriptor typeObjectDescriptor = (TypeObjectDescriptor) valueType;

            JSONObject jsonObject = componentJsonObject.getJSONObject(propertyName);
            jsonObject.keySet().forEach(key -> {
                MapComponentDataHolder dataHolder = new MapComponentDataHolder(keyAndValues);
                TypeObjectDeserializer.get().deserialize(jsonObject, dataHolder, key, typeObjectDescriptor);
            });
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
        public <T> T get(String s) {
            return (T) delegate.get(s);
        }

        @Override
        public void set(String s, Object o) {
            delegate.put(s, (TypeObjectDescriptor.TypeObject) o);
        }

        @Override
        public boolean has(String s) {
            return delegate.containsKey(s);
        }
    }
}
