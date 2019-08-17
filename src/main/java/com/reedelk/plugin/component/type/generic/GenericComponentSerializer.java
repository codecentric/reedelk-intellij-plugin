package com.reedelk.plugin.component.type.generic;

import com.reedelk.plugin.commons.JsonObjectFactory;
import com.reedelk.plugin.component.domain.ComponentData;
import com.reedelk.plugin.component.domain.ComponentDataHolder;
import com.reedelk.plugin.component.domain.TypeObjectDescriptor;
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

        serialize(componentData, componentAsJson);

        return componentAsJson;
    }

    public static void serialize(ComponentDataHolder componentData, JSONObject componentAsJson) {
        componentData.keys().forEach(propertyName -> {
            Object data = componentData.get(propertyName);
            if (data instanceof TypeObjectDescriptor.TypeObject) {
                serializeTypeObject(componentAsJson, propertyName, (TypeObjectDescriptor.TypeObject) data);
            } else {
                componentAsJson.put(propertyName, data);
            }
        });
    }

    private static void serializeTypeObject(JSONObject parent, String propertyName, TypeObjectDescriptor.TypeObject data) {
        JSONObject nestedObjectJson = JsonObjectFactory.newJSONObject();
        parent.put(propertyName, nestedObjectJson);
        serialize(data, nestedObjectJson);
    }
}
