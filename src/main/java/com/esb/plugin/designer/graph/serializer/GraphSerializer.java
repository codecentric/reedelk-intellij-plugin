package com.esb.plugin.designer.graph.serializer;

import com.esb.internal.commons.JsonParser;
import com.esb.plugin.designer.editor.component.ComponentDescriptor;
import com.esb.plugin.designer.graph.FlowGraph;
import org.json.JSONArray;
import org.json.JSONObject;

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

    private static JSONObject serialize(ComponentDescriptor descriptor) {
        JSONObject componentAsJson = new JSONObject();
        JsonParser.Implementor.name(descriptor.getFullyQualifiedName(), componentAsJson);

        List<String> keys = descriptor.componentDataKeys();
        keys.forEach(propertyName -> {
            Object data = descriptor.getData(propertyName);
            componentAsJson.put(propertyName, data);
        });

        return componentAsJson;
    }
}
