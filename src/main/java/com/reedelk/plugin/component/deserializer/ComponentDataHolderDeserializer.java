package com.reedelk.plugin.component.deserializer;

import com.reedelk.module.descriptor.model.*;
import com.reedelk.plugin.commons.TypeObjectFactory;
import com.reedelk.plugin.converter.ValueConverter;
import com.reedelk.plugin.converter.ValueConverterProvider;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.reedelk.module.descriptor.model.TypeObjectDescriptor.TypeObject;
import static com.reedelk.runtime.commons.JsonParser.Component;

public class ComponentDataHolderDeserializer {

    private static final ComponentDataHolderDeserializer INSTANCE = new ComponentDataHolderDeserializer();

    public static ComponentDataHolderDeserializer get() {
        return INSTANCE;
    }

    private ComponentDataHolderDeserializer() {
    }

    class MapComponentDataHolder implements ComponentDataHolder {

        private final Map<String, TypeObject> delegate;

        public MapComponentDataHolder(Map<String, TypeObject> delegate) {
            this.delegate = delegate;
        }

        @Override
        public List<String> keys() {
            return new ArrayList<>(delegate.keySet());
        }

        @Override
        public <T> T get(String s) {
            return (T) delegate.get(s);
        }

        @Override
        public void set(String s, Object o) {
            delegate.put(s, (TypeObject) o);
        }

        @Override
        public boolean has(String s) {
            return delegate.containsKey(s);
        }
    }

    public void deserialize(@NotNull JSONObject componentJsonObject,
                            @NotNull ComponentDataHolder componentData,
                            @NotNull PropertyDescriptor propertyDescriptor) {
        TypeDescriptor propertyType = propertyDescriptor.getType();

        if (propertyType instanceof TypeObjectDescriptor) {
            deserializeTypeObjectDescriptor(componentJsonObject,
                    componentData,
                    propertyDescriptor.getName(),
                    (TypeObjectDescriptor) propertyType);

        } else if (propertyType instanceof TypeMapDescriptor) {

            TypeDescriptor valueType = ((TypeMapDescriptor) propertyType).getValueType();
            if (valueType instanceof TypeObjectDescriptor) {
                String name = propertyDescriptor.getName();

                // Responses is Map<String, OpenApiResponse> type.
                Map<String, TypeObject> keyAndValues = new HashMap<>();
                componentData.set(name, keyAndValues);

                // Process each response.
                // TypeObjectDescriptor for OpenApiResponse
                TypeObjectDescriptor typeObjectDescriptor = (TypeObjectDescriptor) valueType;

                JSONObject jsonObject = componentJsonObject.getJSONObject(name); // responses
                jsonObject.keySet().forEach(key -> {
                    JSONObject objectContent = jsonObject.getJSONObject(key);

                    MapComponentDataHolder dh = new MapComponentDataHolder(keyAndValues);
                    deserializeTypeObjectDescriptor(jsonObject,dh, key, typeObjectDescriptor);
                });
            } else {
                ValueConverter<?> converter = ValueConverterProvider.forType(propertyType);
                Object propertyValue = converter.from(propertyDescriptor.getName(), componentJsonObject);
                componentData.set(propertyDescriptor.getName(), propertyValue);
            }

        } else {
            ValueConverter<?> converter = ValueConverterProvider.forType(propertyType);
            Object propertyValue = converter.from(propertyDescriptor.getName(), componentJsonObject);
            componentData.set(propertyDescriptor.getName(), propertyValue);
        }
    }

    private void deserializeTypeObjectDescriptor(@NotNull JSONObject jsonObject,
                                                 @NotNull ComponentDataHolder componentData,
                                                 @NotNull String propertyName,
                                                 @NotNull TypeObjectDescriptor propertyType) {

        TypeObject nestedObject = TypeObjectFactory.newInstance(propertyType);

        if (jsonObject.has(propertyName) && !jsonObject.isNull(propertyName)) {

            JSONObject nestedJsonObject = jsonObject.getJSONObject(propertyName);

            // If the property is present in the JSON but it is an empty object we
            // still fill up instances of type object for object properties recursively.
            // This is needed, to enable the UI to fill up the values in the
            // Type Object properties when the user edits a value.
            // This happens for instance when we have a JSON object like the following:
            // "configuration": {}
            if (nestedJsonObject.isEmpty()) {
                addEmptyInstancesForTypeObject(componentData, propertyName, propertyType);

            } else if (Shared.YES.equals(propertyType.getShared())) {
                // The config is shareable, therefore we just set the
                // reference value pointing to the shared config.
                if (nestedJsonObject.has(Component.ref())) {
                    String reference = Component.ref(nestedJsonObject);
                    nestedObject.set(Component.ref(), reference);
                    componentData.set(propertyName, nestedObject);
                } else {
                    // The nested JSON object does not have a component "ref" property inside it.
                    // We still must add empty instances for the type object.
                    addEmptyInstancesForTypeObject(componentData, propertyName, propertyType);
                }
            } else {
                // The config is not shareable, hence we deserialize the object right away.
                propertyType.getObjectProperties().forEach(typeDescriptor ->
                        deserialize(nestedJsonObject, nestedObject, typeDescriptor));
                componentData.set(propertyName, nestedObject);
            }

        } else {
            // If the property is not present in the JSON we still fill up
            // Instances of type object for object properties recursively.
            // This is needed, to enable the UI to fill up the values in the
            // Type Object properties when the user edits a value.
            addEmptyInstancesForTypeObject(componentData, propertyName, propertyType);
        }
    }

    private void addEmptyInstancesForTypeObject(ComponentDataHolder dataHolder, String propertyName, TypeDescriptor descriptor) {
        if (descriptor instanceof TypeObjectDescriptor) {
            TypeObjectDescriptor propertyObjectType = (TypeObjectDescriptor) descriptor;
            TypeObject typeObject = TypeObjectFactory.newInstance(propertyObjectType);

            dataHolder.set(propertyName, typeObject);
            // From now on, the subtree contains null objects.
            propertyObjectType.getObjectProperties()
                    .forEach(propertyDescriptor ->
                            addEmptyInstancesForTypeObject(typeObject, propertyDescriptor.getName(), propertyDescriptor.getType()));
        }
    }
}
