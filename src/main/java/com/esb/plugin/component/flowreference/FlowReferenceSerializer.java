package com.esb.plugin.component.flowreference;

import com.esb.plugin.component.ComponentData;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.serializer.AbstractNodeSerializer;
import com.esb.plugin.graph.serializer.JsonObjectFactory;
import org.json.JSONObject;

import static com.esb.internal.commons.JsonParser.FlowReference;
import static com.esb.internal.commons.JsonParser.Implementor;

public class FlowReferenceSerializer extends AbstractNodeSerializer {

    @Override
    protected JSONObject serializeNode(FlowGraph graph, GraphNode node, GraphNode stop) {

        ComponentData componentData = node.componentData();

        JSONObject componentAsJson = JsonObjectFactory.newJSONObject();

        Implementor.name(componentData.getFullyQualifiedName(), componentAsJson);

        String ref = (String) componentData.get(FlowReference.ref());

        componentAsJson.put(FlowReference.ref(), ref);

        return componentAsJson;
    }
}
