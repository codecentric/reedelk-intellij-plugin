package com.esb.plugin.component.generic;

import com.esb.plugin.component.ComponentData;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.serializer.AbstractSerializer;
import com.esb.plugin.graph.serializer.SerializerUtilities;
import org.json.JSONObject;

import static com.esb.internal.commons.JsonParser.Implementor;

public class GenericComponentSerializer extends AbstractSerializer {

    @Override
    public JSONObject serialize(FlowGraph graph, GraphNode node) {
        ComponentData componentData = node.component();

        JSONObject componentAsJson = SerializerUtilities.newJSONObject();

        Implementor.name(componentData.getFullyQualifiedName(), componentAsJson);

        componentData.descriptorProperties().forEach(propertyName -> {
            Object data = componentData.get(propertyName);
            if (data == null) {
                data = JSONObject.NULL;
            }
            componentAsJson.put(propertyName.toLowerCase(), data);
        });

        return componentAsJson;
    }
}