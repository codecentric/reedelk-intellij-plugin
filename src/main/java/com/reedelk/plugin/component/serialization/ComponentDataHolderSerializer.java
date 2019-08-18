package com.reedelk.plugin.component.serialization;

import com.reedelk.plugin.commons.JsonObjectFactory;
import com.reedelk.plugin.component.domain.ComponentDataHolder;
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
        parent.put(propertyName, nestedObjectJson);
        serialize(typeObject, nestedObjectJson);
    }
}
