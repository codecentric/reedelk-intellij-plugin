package com.esb.plugin.component.unknown;

import com.esb.plugin.component.ComponentData;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.serializer.AbstractNodeSerializer;
import com.esb.plugin.graph.serializer.JsonObjectFactory;
import org.json.JSONObject;

public class UnknownSerializer extends AbstractNodeSerializer {

    /**
     * We just serialize back all the properties (we must keep the original definition)
     */
    @Override
    protected JSONObject serializeNode(FlowGraph graph, GraphNode node, GraphNode stop) {
        ComponentData componentData = node.componentData();

        JSONObject componentAsJson = JsonObjectFactory.newJSONObject();

        componentData.getDataProperties().forEach(propertyName -> {

            Object data = componentData.get(propertyName);

            componentAsJson.put(propertyName, data);

        });

        return componentAsJson;
    }
}
