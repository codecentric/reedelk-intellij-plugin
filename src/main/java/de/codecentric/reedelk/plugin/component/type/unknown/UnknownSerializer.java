package de.codecentric.reedelk.plugin.component.type.unknown;

import de.codecentric.reedelk.plugin.commons.JsonObjectFactory;
import de.codecentric.reedelk.plugin.component.ComponentData;
import de.codecentric.reedelk.plugin.graph.FlowGraph;
import de.codecentric.reedelk.plugin.graph.node.GraphNode;
import de.codecentric.reedelk.plugin.graph.serializer.AbstractNodeSerializer;
import org.json.JSONObject;

import static de.codecentric.reedelk.runtime.commons.JsonParser.Implementor;

public class UnknownSerializer extends AbstractNodeSerializer {

    /**
     * We just serialize back all the properties (we must keep the original definition)
     */
    @Override
    protected JSONObject serializeNode(FlowGraph graph, GraphNode node, GraphNode stop) {
        ComponentData componentData = node.componentData();

        JSONObject componentAsJson = JsonObjectFactory.newJSONObject();


        Implementor.name(componentData.get(Implementor.name()), componentAsJson);

        componentData
                .getDataProperties()
                .stream()
                .filter(propertyName -> !propertyName.equals(Implementor.name()))
                .forEach(propertyName -> {

                    Object data = componentData.get(propertyName);

                    componentAsJson.put(propertyName, data);

                });

        return componentAsJson;
    }
}
