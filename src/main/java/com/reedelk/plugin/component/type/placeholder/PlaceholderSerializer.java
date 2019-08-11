package com.reedelk.plugin.component.type.placeholder;

import com.reedelk.plugin.commons.JsonObjectFactory;
import com.reedelk.plugin.component.domain.ComponentData;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.graph.serializer.AbstractNodeSerializer;
import com.reedelk.runtime.component.Placeholder;
import org.json.JSONObject;

import static com.reedelk.runtime.commons.JsonParser.Implementor;
import static com.reedelk.runtime.commons.Preconditions.checkState;

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
