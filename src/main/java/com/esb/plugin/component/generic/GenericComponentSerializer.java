package com.esb.plugin.component.generic;

import com.esb.plugin.component.ComponentData;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.serializer.AbstractSerializer;
import com.esb.plugin.graph.serializer.JSONObjectFactory;
import org.json.JSONObject;

import static com.esb.internal.commons.JsonParser.Implementor;

public class GenericComponentSerializer extends AbstractSerializer {

    @Override
    public JSONObject serialize(FlowGraph graph, GraphNode node) {

        ComponentData componentData = node.component();

        JSONObject componentAsJson = JSONObjectFactory.newJSONObject();

        Implementor.name(componentData.getFullyQualifiedName(), componentAsJson);

        componentData.descriptorProperties().forEach(propertyName -> {

            Object data = componentData.getOrDefault(propertyName.toLowerCase(), JSONObject.NULL);

            componentAsJson.put(propertyName.toLowerCase(), data);
        });

        return componentAsJson;
    }
}
