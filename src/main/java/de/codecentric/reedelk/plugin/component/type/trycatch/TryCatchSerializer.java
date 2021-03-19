package de.codecentric.reedelk.plugin.component.type.trycatch;

import de.codecentric.reedelk.plugin.commons.JsonObjectFactory;
import de.codecentric.reedelk.plugin.component.ComponentData;
import de.codecentric.reedelk.plugin.graph.FlowGraph;
import de.codecentric.reedelk.plugin.graph.node.GraphNode;
import de.codecentric.reedelk.plugin.graph.node.ScopedGraphNode;
import de.codecentric.reedelk.plugin.graph.serializer.AbstractScopedNodeSerializer;
import de.codecentric.reedelk.plugin.graph.serializer.SerializerFactory;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import static de.codecentric.reedelk.runtime.commons.JsonParser.Implementor;
import static de.codecentric.reedelk.runtime.commons.JsonParser.TryCatch;

public class TryCatchSerializer extends AbstractScopedNodeSerializer {

    @Override
    protected JSONObject serializeScopedNode(FlowGraph graph, ScopedGraphNode tryCatchNode, GraphNode stop) {

        ComponentData componentData = tryCatchNode.componentData();

        JSONObject tryCatchObject = JsonObjectFactory.newJSONObject();

        Implementor.name(componentData.getFullyQualifiedName(), tryCatchObject);

        List<GraphNode> successorsOfTryCatch = graph.successors(tryCatchNode);


        // Handle special cases where successor is 1 and it is equal to the stop node.

        if (successorsOfTryCatch.isEmpty()) {
            return tryCatchObject;
        }

        if (successorsOfTryCatch.size() == 1 && successorsOfTryCatch.get(0) == stop) {
            return tryCatchObject;
        }

        JSONArray nextArrayObject = new JSONArray();

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
