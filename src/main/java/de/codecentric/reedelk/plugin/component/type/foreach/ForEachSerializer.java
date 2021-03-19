package de.codecentric.reedelk.plugin.component.type.foreach;

import de.codecentric.reedelk.plugin.commons.JsonObjectFactory;
import de.codecentric.reedelk.plugin.component.ComponentData;
import de.codecentric.reedelk.plugin.component.serializer.ComponentDataHolderSerializer;
import de.codecentric.reedelk.plugin.graph.FlowGraph;
import de.codecentric.reedelk.plugin.graph.node.GraphNode;
import de.codecentric.reedelk.plugin.graph.node.ScopedGraphNode;
import de.codecentric.reedelk.plugin.graph.serializer.AbstractScopedNodeSerializer;
import de.codecentric.reedelk.plugin.graph.serializer.SerializerFactory;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import static de.codecentric.reedelk.runtime.commons.JsonParser.ForEach;
import static de.codecentric.reedelk.runtime.commons.JsonParser.Implementor;

public class ForEachSerializer extends AbstractScopedNodeSerializer {

    @Override
    protected JSONObject serializeScopedNode(FlowGraph graph, ScopedGraphNode forEachNode, GraphNode stop) {

        ComponentData componentData = forEachNode.componentData();

        JSONObject forEachObject = JsonObjectFactory.newJSONObject();

        Implementor.name(componentData.getFullyQualifiedName(), forEachObject);

        ComponentDataHolderSerializer.get().serialize(componentData, forEachObject);

        List<GraphNode> successorsOfForEach = graph.successors(forEachNode);

        JSONArray forEachArray = new JSONArray();

        for (GraphNode successor : successorsOfForEach) {
            // This case happens when the fork node has no successors inside the scope,
            // but exists a successor right outside its scope. In this case we must stop
            // serializing.
            if (successor == stop) continue;

            SerializerFactory.get().node(successor)
                    .build()
                    .serialize(graph, forEachArray, successor, stop);

            ForEach.next(forEachArray, forEachObject);
        }

        ForEach.next(forEachArray, forEachObject);

        return forEachObject;
    }
}
