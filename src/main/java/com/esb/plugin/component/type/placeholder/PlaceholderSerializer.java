package com.esb.plugin.component.type.placeholder;

import com.esb.plugin.component.domain.ComponentData;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.ScopedGraphNode;
import com.esb.plugin.graph.serializer.AbstractScopedNodeSerializer;
import com.esb.plugin.graph.serializer.JsonObjectFactory;
import com.esb.system.component.Placeholder;
import org.json.JSONObject;

import static com.esb.internal.commons.JsonParser.Implementor;
import static com.esb.internal.commons.Preconditions.checkState;

public class PlaceholderSerializer extends AbstractScopedNodeSerializer {

    @Override
    protected JSONObject serializeScopedNode(FlowGraph graph, ScopedGraphNode scopedNode, GraphNode stop) {
        ComponentData componentData = scopedNode.componentData();
        String componentFullyQualifiedName = componentData.getFullyQualifiedName();
        checkState(componentFullyQualifiedName.equals(Placeholder.class.getName()),
                "Must have same fully qualified name");

        JSONObject componentAsJson = JsonObjectFactory.newJSONObject();

        Implementor.name(componentFullyQualifiedName, componentAsJson);

        return componentAsJson;
    }
}
