package com.esb.plugin.component.type.placeholder;

import com.esb.plugin.component.domain.ComponentData;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.serializer.AbstractNodeSerializer;
import com.esb.plugin.graph.serializer.JsonObjectFactory;
import com.esb.system.component.Placeholder;
import org.json.JSONObject;

import static com.esb.internal.commons.JsonParser.Implementor;
import static com.esb.internal.commons.Preconditions.checkState;

public class PlaceholderSerializer extends AbstractNodeSerializer {

    @Override
    protected JSONObject serializeNode(FlowGraph graph, GraphNode node, GraphNode stop) {
        ComponentData componentData = node.componentData();
        String componentFullyQualifiedName = componentData.getFullyQualifiedName();
        checkState(componentFullyQualifiedName.equals(Placeholder.class.getName()),
                "Must have same fully qualified name");

        JSONObject componentAsJson = JsonObjectFactory.newJSONObject();

        Implementor.name(componentFullyQualifiedName, componentAsJson);

        return componentAsJson;
    }
}
