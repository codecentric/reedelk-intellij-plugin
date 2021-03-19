package de.codecentric.reedelk.plugin.component.type.fork;

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

import static de.codecentric.reedelk.runtime.commons.JsonParser.Fork;
import static de.codecentric.reedelk.runtime.commons.JsonParser.Implementor;


public class ForkSerializer extends AbstractScopedNodeSerializer {

    @Override
    protected JSONObject serializeScopedNode(FlowGraph graph, ScopedGraphNode forkNode, GraphNode stop) {
        ComponentData componentData = forkNode.componentData();

        JSONObject forkObject = JsonObjectFactory.newJSONObject();

        Implementor.name(componentData.getFullyQualifiedName(), forkObject);

        List<GraphNode> successorsOfFork = graph.successors(forkNode);

        JSONArray forkArrayObject = new JSONArray();

        for (GraphNode successor : successorsOfFork) {
            // This case happens when the fork node has no successors inside the scope,
            // but exists a successor right outside its scope. In this case we must stop
            // serializing.
            if (successor == stop) continue;

            JSONObject nextObject = JsonObjectFactory.newJSONObject();

            JSONArray nextArrayObject = new JSONArray();

            SerializerFactory.get().node(successor)
                    .build()
                    .serialize(graph, nextArrayObject, successor, stop);

            Fork.next(nextArrayObject, nextObject);

            forkArrayObject.put(nextObject);
        }

        Fork.fork(forkArrayObject, forkObject);

        return forkObject;
    }
}
