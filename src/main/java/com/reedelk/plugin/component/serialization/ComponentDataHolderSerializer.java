package com.reedelk.plugin.component.serialization;

import com.reedelk.plugin.commons.JsonObjectFactory;
import com.reedelk.plugin.component.domain.ComponentDataHolder;
import com.reedelk.plugin.component.domain.TypeObjectDescriptor;
import org.json.JSONObject;

public class ComponentDataHolderSerializer {

    // TODO: If the Object is empty it should not serialize the object...
    public static void serialize(ComponentDataHolder componentData, JSONObject componentJsonObject) {
        componentData.keys().forEach(propertyName -> {
            Object data = componentData.get(propertyName);
            if (data instanceof TypeObjectDescriptor.TypeObject) {
                serializeTypeObject(componentJsonObject, propertyName, (TypeObjectDescriptor.TypeObject) data);
            } else {
                componentJsonObject.put(propertyName, data);
            }
        });
    }

    private static void serializeTypeObject(JSONObject parent, String propertyName, TypeObjectDescriptor.TypeObject data) {
        JSONObject nestedObjectJson = JsonObjectFactory.newJSONObject();
        parent.put(propertyName, nestedObjectJson);
        serialize(data, nestedObjectJson);
    }
}
