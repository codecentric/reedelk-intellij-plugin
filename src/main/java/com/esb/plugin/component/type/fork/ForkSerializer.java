package com.esb.plugin.component.type.fork;

import com.esb.plugin.commons.JsonObjectFactory;
import com.esb.plugin.component.domain.ComponentData;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.ScopedGraphNode;
import com.esb.plugin.graph.serializer.AbstractScopedNodeSerializer;
import com.esb.plugin.graph.serializer.SerializerFactory;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import static com.esb.internal.commons.JsonParser.Fork;
import static com.esb.internal.commons.JsonParser.Implementor;

public class ForkSerializer extends AbstractScopedNodeSerializer {

    @Override
    protected JSONObject serializeScopedNode(FlowGraph graph, ScopedGraphNode forkNode, GraphNode stop) {
        ComponentData componentData = forkNode.componentData();

        JSONObject forkObject = JsonObjectFactory.newJSONObject();

        Implementor.name(componentData.getFullyQualifiedName(), forkObject);

        // TODO: If  thread pool size from the component is null, this one trows an exception
        Integer threadPoolSize = componentData.get(Fork.threadPoolSize());

        Fork.threadPoolSize(threadPoolSize, forkObject);

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
