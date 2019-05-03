package com.esb.plugin.component.generic;

import com.esb.internal.commons.JsonParser;
import com.esb.plugin.component.Component;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.serializer.AbstractSerializer;
import org.json.JSONObject;

import java.util.LinkedHashMap;

public class GenericComponentSerializer extends AbstractSerializer {
    @Override
    public JSONObject serialize(GraphNode node) {
        Component component = node.component();

        LinkedHashMap<String, String> jsonOrderedMap = new LinkedHashMap<>();
        JSONObject componentAsJson = new JSONObject(jsonOrderedMap);
        JsonParser.Implementor.name(component.getFullyQualifiedName(), componentAsJson);

        component.getPropertiesNames().forEach(propertyName -> {
            Object data = component.getData(propertyName);
            if (data == null) {
                data = JSONObject.NULL;
            }
            componentAsJson.put(propertyName.toLowerCase(), data);
        });

        return componentAsJson;
    }
}
