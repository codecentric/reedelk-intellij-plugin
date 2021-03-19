package de.codecentric.reedelk.plugin.component.type.flowreference;

import de.codecentric.reedelk.plugin.commons.JsonObjectFactory;
import de.codecentric.reedelk.plugin.component.ComponentData;
import de.codecentric.reedelk.plugin.graph.FlowGraph;
import de.codecentric.reedelk.plugin.graph.node.GraphNode;
import de.codecentric.reedelk.plugin.graph.serializer.AbstractNodeSerializer;
import org.json.JSONObject;

import static de.codecentric.reedelk.runtime.commons.JsonParser.FlowReference;
import static de.codecentric.reedelk.runtime.commons.JsonParser.Implementor;

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
