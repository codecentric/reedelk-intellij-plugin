package de.codecentric.reedelk.plugin.component.type.generic;

import de.codecentric.reedelk.plugin.commons.JsonObjectFactory;
import de.codecentric.reedelk.plugin.component.ComponentData;
import de.codecentric.reedelk.plugin.component.serializer.ComponentDataHolderSerializer;
import de.codecentric.reedelk.plugin.graph.FlowGraph;
import de.codecentric.reedelk.plugin.graph.node.GraphNode;
import de.codecentric.reedelk.plugin.graph.serializer.AbstractNodeSerializer;
import org.json.JSONObject;

import static de.codecentric.reedelk.runtime.commons.JsonParser.Implementor;

public class GenericComponentSerializer extends AbstractNodeSerializer {

    @Override
    protected JSONObject serializeNode(FlowGraph graph, GraphNode node, GraphNode stop) {

        ComponentData componentData = node.componentData();

        JSONObject componentAsJson = JsonObjectFactory.newJSONObject();

        Implementor.name(componentData.getFullyQualifiedName(), componentAsJson);

        ComponentDataHolderSerializer.get().serialize(componentData, componentAsJson);

        return componentAsJson;
    }
}
