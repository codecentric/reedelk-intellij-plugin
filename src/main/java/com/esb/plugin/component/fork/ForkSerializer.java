package com.esb.plugin.component.fork;

import com.esb.plugin.component.ComponentData;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.serializer.AbstractSerializer;
import com.esb.plugin.graph.serializer.GraphSerializer;
import com.esb.plugin.graph.serializer.JsonObjectFactory;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import static com.esb.internal.commons.JsonParser.Fork;
import static com.esb.internal.commons.JsonParser.Implementor;

public class ForkSerializer extends AbstractSerializer {

    @Override
    public JSONObject serialize(FlowGraph graph, GraphNode node, GraphNode stop) {

        ComponentData componentData = node.componentData();

        JSONObject forkObject = JsonObjectFactory.newJSONObject();

        Implementor.name(componentData.getFullyQualifiedName(), forkObject);

        Integer threadPoolSize = (Integer) componentData.get(Fork.threadPoolSize());

        if (threadPoolSize == null) {
            // TODO: This is wrong, here we should use default value.
            Fork.threadPoolSize(0, forkObject);
        } else {
            Fork.threadPoolSize(threadPoolSize, forkObject);
        }

        List<GraphNode> successorsOfFork = graph.successors(node);

        JSONArray forkArrayObject = new JSONArray();

        for (GraphNode successor : successorsOfFork) {

            JSONObject nextObject = JsonObjectFactory.newJSONObject();

            JSONArray nextArrayObject = new JSONArray();

            GraphSerializer.doSerialize(graph, nextArrayObject, successor, stop);

            Fork.next(nextArrayObject, nextObject);

            forkArrayObject.put(nextObject);

        }

        Fork.fork(forkArrayObject, forkObject);

        return forkObject;
    }
}
