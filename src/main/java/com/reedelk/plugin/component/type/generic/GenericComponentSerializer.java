package com.reedelk.plugin.component.type.generic;

import com.reedelk.plugin.commons.JsonObjectFactory;
import com.reedelk.plugin.component.domain.ComponentData;
import com.reedelk.plugin.component.serialization.ComponentDataHolderSerializer;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.graph.serializer.AbstractNodeSerializer;
import org.json.JSONObject;

import static com.reedelk.runtime.commons.JsonParser.Implementor;

public class GenericComponentSerializer extends AbstractNodeSerializer {

    @Override
    protected JSONObject serializeNode(FlowGraph graph, GraphNode node, GraphNode stop) {

        ComponentData componentData = node.componentData();

        JSONObject componentAsJson = JsonObjectFactory.newJSONObject();

        Implementor.name(componentData.getFullyQualifiedName(), componentAsJson);

        ComponentDataHolderSerializer.serialize(componentData, componentAsJson);

        return componentAsJson;
    }
}
