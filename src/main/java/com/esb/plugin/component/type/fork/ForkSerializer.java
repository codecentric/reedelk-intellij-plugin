package com.esb.plugin.component.type.fork;

import com.esb.plugin.component.domain.ComponentData;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.ScopedGraphNode;
import com.esb.plugin.graph.serializer.AbstractScopedNodeSerializer;
import com.esb.plugin.graph.serializer.GraphSerializerFactory;
import com.esb.plugin.graph.serializer.JsonObjectFactory;
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

        Integer threadPoolSize = (Integer) componentData.get(Fork.threadPoolSize());

        if (threadPoolSize == null) {
            // TODO: This is wrong, here we should use default value.
            Fork.threadPoolSize(3, forkObject);
        } else {
            Fork.threadPoolSize(threadPoolSize, forkObject);
        }

        List<GraphNode> successorsOfFork = graph.successors(forkNode);

        JSONArray forkArrayObject = new JSONArray();

        for (GraphNode successor : successorsOfFork) {

            JSONObject nextObject = JsonObjectFactory.newJSONObject();

            JSONArray nextArrayObject = new JSONArray();

            GraphSerializerFactory.get().node(successor)
                    .build()
                    .serialize(graph, nextArrayObject, successor, stop);

            Fork.next(nextArrayObject, nextObject);

            forkArrayObject.put(nextObject);
        }

        Fork.fork(forkArrayObject, forkObject);

        return forkObject;
    }
}
