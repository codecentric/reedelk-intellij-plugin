package com.reedelk.plugin.component.serialization;

import com.reedelk.plugin.commons.JsonObjectFactory;
import com.reedelk.plugin.component.domain.ComponentDataHolder;
import com.reedelk.runtime.commons.JsonParser;
import org.json.JSONObject;

import static com.reedelk.plugin.component.domain.TypeObjectDescriptor.TypeObject;

public class ComponentDataHolderSerializer {

    public static void serialize(ComponentDataHolder componentData, JSONObject componentJsonObject) {
        componentData.keys().forEach(propertyName -> {
            Object data = componentData.get(propertyName);
            if (data instanceof TypeObject) {
                TypeObject typeObject = (TypeObject) data;
                serializeTypeObject(componentJsonObject, propertyName, typeObject);
            } else {
                componentJsonObject.put(propertyName, data);
            }
        });
    }

    private static void serializeTypeObject(JSONObject parent, String propertyName, TypeObject typeObject) {
        JSONObject nestedObjectJson = JsonObjectFactory.newJSONObject();

        serialize(typeObject, nestedObjectJson);

        // We just add the serialized children if the JSON objects are not empty. Otherwise
        // we would serialize nested objects with just the implementor property.
        if (isNotEmpty(nestedObjectJson) && doesNotContainOnlyImplementorProperty(nestedObjectJson)) {
            parent.put(propertyName, nestedObjectJson);
        }
    }

    static boolean doesNotContainOnlyImplementorProperty(JSONObject nestedObjectJson) {
        boolean containsImplementor = nestedObjectJson.has(JsonParser.Implementor.name());
        boolean containsOnlyOneElement = nestedObjectJson.keySet().size() == 1;
        return !(containsImplementor && containsOnlyOneElement);
    }

    static boolean isNotEmpty(JSONObject nestedObjectJson) {
        return !nestedObjectJson.isEmpty();
    }
}
