package com.esb.plugin.component.generic;

import com.esb.plugin.component.Component;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.serializer.AbstractSerializer;
import com.esb.plugin.graph.serializer.SerializerUtilities;
import org.json.JSONObject;

import static com.esb.internal.commons.JsonParser.Implementor;

public class GenericComponentSerializer extends AbstractSerializer {

    @Override
    public JSONObject serialize(GraphNode node) {
        Component component = node.component();

        JSONObject componentAsJson = SerializerUtilities.newJSONObject();

        Implementor.name(component.getFullyQualifiedName(), componentAsJson);

        component.getPropertiesNames().forEach(propertyName -> {
            Object data = component.getData(propertyName);
            if (data == null) {
                data = JSONObject.NULL;
            }
            componentAsJson.put(propertyName.toLowerCase(), data);
        });

        return componentAsJson;
    }
}
