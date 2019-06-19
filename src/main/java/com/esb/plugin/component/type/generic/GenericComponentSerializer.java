package com.esb.plugin.component.type.generic;

import com.esb.internal.commons.JsonParser;
import com.esb.plugin.component.domain.ComponentData;
import com.esb.plugin.component.domain.ComponentDataHolder;
import com.esb.plugin.component.domain.TypeObjectDescriptor;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.serializer.AbstractNodeSerializer;
import com.esb.plugin.graph.serializer.JsonObjectFactory;
import org.json.JSONObject;

import static com.esb.internal.commons.JsonParser.Implementor;

public class GenericComponentSerializer extends AbstractNodeSerializer {

    @Override
    protected JSONObject serializeNode(FlowGraph graph, GraphNode node, GraphNode stop) {

        ComponentData componentData = node.componentData();

        JSONObject componentAsJson = JsonObjectFactory.newJSONObject();

        Implementor.name(componentData.getFullyQualifiedName(), componentAsJson);

        serialize(componentData, componentAsJson);

        return componentAsJson;
    }

    private void serialize(ComponentDataHolder componentData, JSONObject parent) {
        componentData.keys().forEach(propertyName -> {
            Object data = componentData.get(propertyName);
            if (data instanceof TypeObjectDescriptor.TypeObject) {
                TypeObjectDescriptor.TypeObject object = (TypeObjectDescriptor.TypeObject) data;
                JSONObject nestedObject = JsonObjectFactory.newJSONObject();
                nestedObject.put(JsonParser.Implementor.name(), object.getTypeFullyQualifiedName());
                parent.put(propertyName, nestedObject);
                serialize(object, nestedObject);
            } else {
                parent.put(propertyName, data);
            }
        });
    }
}
