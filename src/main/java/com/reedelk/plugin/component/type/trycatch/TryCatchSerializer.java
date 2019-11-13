package com.reedelk.plugin.component.type.trycatch;

import com.reedelk.plugin.commons.JsonObjectFactory;
import com.reedelk.plugin.component.domain.ComponentData;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.graph.node.ScopedGraphNode;
import com.reedelk.plugin.graph.serializer.AbstractScopedNodeSerializer;
import com.reedelk.plugin.graph.serializer.SerializerFactory;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import static com.reedelk.runtime.commons.JsonParser.Implementor;
import static com.reedelk.runtime.commons.JsonParser.TryCatch;

public class TryCatchSerializer extends AbstractScopedNodeSerializer {

    @Override
    protected JSONObject serializeScopedNode(FlowGraph graph, ScopedGraphNode tryCatchNode, GraphNode stop) {

        ComponentData componentData = tryCatchNode.componentData();

        JSONObject tryCatchObject = JsonObjectFactory.newJSONObject();

        Implementor.name(componentData.getFullyQualifiedName(), tryCatchObject);

        List<GraphNode> successorsOfTryCatch = graph.successors(tryCatchNode);


        // Handle special cases where successor is 1 and it is equal to the stop node.

        if (successorsOfTryCatch.size() == 0) {
            return tryCatchObject;
        }

        if (successorsOfTryCatch.size() == 1 && successorsOfTryCatch.get(0) == stop) {
            return tryCatchObject;
        }

        JSONArray nextArrayObject = new JSONArray();

        successorsOfTryCatch.size();
        GraphNode trySubflow = successorsOfTryCatch.get(0);


        SerializerFactory.get().node(trySubflow)
                .build()
                .serialize(graph, nextArrayObject, trySubflow, stop);

        TryCatch.doTry(nextArrayObject, tryCatchObject);

        nextArrayObject = new JSONArray();

        if (successorsOfTryCatch.size() > 1) {
            GraphNode catchSubflow = successorsOfTryCatch.get(1);

            SerializerFactory.get().node(catchSubflow)
                    .build()
                    .serialize(graph, nextArrayObject, catchSubflow, stop);
        }

        TryCatch.doCatch(nextArrayObject, tryCatchObject);

        return tryCatchObject;
    }
}