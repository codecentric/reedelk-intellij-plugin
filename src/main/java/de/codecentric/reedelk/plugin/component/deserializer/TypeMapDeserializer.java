package de.codecentric.reedelk.plugin.component.deserializer;

import de.codecentric.reedelk.module.descriptor.model.component.ComponentDataHolder;
import de.codecentric.reedelk.module.descriptor.model.property.MapDescriptor;
import de.codecentric.reedelk.module.descriptor.model.property.ObjectDescriptor;
import de.codecentric.reedelk.module.descriptor.model.property.PropertyTypeDescriptor;
import de.codecentric.reedelk.plugin.converter.ValueConverter;
import de.codecentric.reedelk.plugin.converter.ValueConverterProvider;
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
                     @NotNull MapDescriptor propertyType) {

        // The deserialization depends on the value type of the map.
        // The value type could be a complex or a primitive type.
        PropertyTypeDescriptor valueType = propertyType.getValueType();
        if (valueType instanceof ObjectDescriptor) {
            // The user defined map looks like: Map<String, OpenApiResponse> where OpenApiResponse is a user
            // defined type implementing 'implementor' interface.
            Map<String, ObjectDescriptor.TypeObject> keyAndValues = new HashMap<>();
            componentData.set(propertyName, keyAndValues);

            // Process each response type.
            ObjectDescriptor typeObjectDescriptor = (ObjectDescriptor) valueType;

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

        private final Map<String, ObjectDescriptor.TypeObject> delegate;

        public MapComponentDataHolder(Map<String, ObjectDescriptor.TypeObject> delegate) {
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
            delegate.put(key, (ObjectDescriptor.TypeObject) value);
        }

        @Override
        public boolean has(String key) {
            return delegate.containsKey(key);
        }
    }
}
