package com.reedelk.plugin.component.type.foreach;

import com.reedelk.plugin.commons.JsonObjectFactory;
import com.reedelk.plugin.component.ComponentData;
import com.reedelk.plugin.component.serializer.ComponentDataHolderSerializer;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.graph.node.ScopedGraphNode;
import com.reedelk.plugin.graph.serializer.AbstractScopedNodeSerializer;
import com.reedelk.plugin.graph.serializer.SerializerFactory;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import static com.reedelk.runtime.commons.JsonParser.ForEach;
import static com.reedelk.runtime.commons.JsonParser.Implementor;

public class ForEachSerializer extends AbstractScopedNodeSerializer {

    @Override
    protected JSONObject serializeScopedNode(FlowGraph graph, ScopedGraphNode forEachNode, GraphNode stop) {

        ComponentData componentData = forEachNode.componentData();

        JSONObject forEachObject = JsonObjectFactory.newJSONObject();

        Implementor.name(componentData.getFullyQualifiedName(), forEachObject);

        ComponentDataHolderSerializer.serialize(componentData, forEachObject);

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