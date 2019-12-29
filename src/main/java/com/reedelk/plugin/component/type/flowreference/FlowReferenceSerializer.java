package com.reedelk.plugin.component.type.flowreference;

import com.reedelk.component.descriptor.ComponentData;
import com.reedelk.plugin.commons.JsonObjectFactory;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.graph.serializer.AbstractNodeSerializer;
import org.json.JSONObject;

import static com.reedelk.runtime.commons.JsonParser.FlowReference;
import static com.reedelk.runtime.commons.JsonParser.Implementor;

public class FlowReferenceSerializer extends AbstractNodeSerializer {

    @Override
    protected JSONObject serializeNode(FlowGraph graph, GraphNode node, GraphNode stop) {

        ComponentData componentData = node.componentData();

        JSONObject componentAsJson = JsonObjectFactory.newJSONObject();

        Implementor.name(componentData.getFullyQualifiedName(), componentAsJson);

        String description = componentData.get(Implementor.description());
        if (description != null) {
            componentAsJson.put(Implementor.description(), description);
        }

        Object ref = componentData.get(FlowReference.ref());
        if (ref == null) {
            ref = FlowReferenceNode.DEFAULT_FLOW_REFERENCE;
        }
        componentAsJson.put(FlowReference.ref(), ref);

        return componentAsJson;
    }
}
