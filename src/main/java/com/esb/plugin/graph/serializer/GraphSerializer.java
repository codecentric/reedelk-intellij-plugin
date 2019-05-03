package com.esb.plugin.graph.serializer;

import com.esb.internal.commons.JsonParser;
import com.esb.plugin.component.Component;
import com.esb.plugin.graph.FlowGraph;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

public class GraphSerializer {

    public static String serialize(FlowGraph graph) {
        JSONArray flow = new JSONArray();
        graph.breadthFirstTraversal(drawable -> {
            JSONObject component = serialize(drawable.component());
            flow.put(component);
        });

        JSONObject flowObject = new JSONObject();
        flowObject.put("id", UUID.randomUUID().toString());
        flowObject.put("flow", flow);
        return flowObject.toString(4);
    }

    private static JSONObject serialize(Component component) {
        LinkedHashMap<String, String> jsonOrderedMap = new LinkedHashMap<>();

        JSONObject componentAsJson = new JSONObject(jsonOrderedMap);
        JsonParser.Implementor.name(component.getFullyQualifiedName(), componentAsJson);

        List<String> keys = component.getPropertiesNames();
        keys.forEach(propertyName -> {
            Object data = component.getData(propertyName);
            if (data == null) {
                data = JSONObject.NULL;
            }
            componentAsJson.put(propertyName.toLowerCase(), data);
        });
        return componentAsJson;
    }
}
