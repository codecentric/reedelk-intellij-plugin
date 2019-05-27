package com.esb.plugin.component.type.placeholder;

import com.esb.plugin.component.domain.ComponentData;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.serializer.AbstractNodeSerializer;
import com.esb.plugin.graph.serializer.JsonObjectFactory;
import org.json.JSONObject;

import static com.esb.internal.commons.JsonParser.Implementor;

public class PlaceholderSerializer extends AbstractNodeSerializer {

    // TODO: Component Data fullyqualified name must be equal to this serializer node type
    @Override
    protected JSONObject serializeNode(FlowGraph graph, GraphNode node, GraphNode stop) {
        ComponentData componentData = node.componentData();

        JSONObject componentAsJson = JsonObjectFactory.newJSONObject();

        Implementor.name(componentData.getFullyQualifiedName(), componentAsJson);

        return componentAsJson;
    }

}
