package com.esb.plugin.component.flowreference;

import com.esb.plugin.component.ComponentData;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.serializer.AbstractSerializer;
import com.esb.plugin.graph.serializer.JSONObjectFactory;
import org.json.JSONObject;

import java.util.UUID;

import static com.esb.internal.commons.JsonParser.Implementor;

public class FlowReferenceSerializer extends AbstractSerializer {

    @Override
    public JSONObject serialize(FlowGraph graph, GraphNode node) {

        ComponentData componentData = node.component();

        JSONObject componentAsJson = JSONObjectFactory.newJSONObject();

        Implementor.name(componentData.getFullyQualifiedName(), componentAsJson);

        componentAsJson.put("ref", UUID.randomUUID().toString());

        return componentAsJson;
    }

}
