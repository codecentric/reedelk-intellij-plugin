package de.codecentric.reedelk.plugin.component.type.fork;

import de.codecentric.reedelk.plugin.component.type.stop.StopNode;
import de.codecentric.reedelk.plugin.graph.FlowGraph;
import de.codecentric.reedelk.plugin.graph.deserializer.AbstractNodeDeserializer;
import de.codecentric.reedelk.plugin.graph.deserializer.DeserializerContext;
import de.codecentric.reedelk.plugin.graph.deserializer.DeserializerFactory;
import de.codecentric.reedelk.plugin.graph.node.GraphNode;
import de.codecentric.reedelk.plugin.graph.utils.CollectNodesBetween;
import de.codecentric.reedelk.runtime.component.Stop;
import org.json.JSONArray;
import org.json.JSONObject;

import static de.codecentric.reedelk.runtime.commons.JsonParser.Fork;

public class ForkDeserializer extends AbstractNodeDeserializer {

    public ForkDeserializer(FlowGraph graph, GraphNode current, DeserializerContext context) {
        super(graph, current, context);
    }

    @Override
    public GraphNode deserialize(GraphNode parent, JSONObject componentDefinition) {

        StopNode stopNode = context.instantiateGraphNode(Stop.class.getName());

        ForkNode forkNode = (ForkNode) current;

        graph.add(parent, forkNode);

        JSONArray fork = Fork.fork(componentDefinition);

        // If the fork does not contain any branch, we immediately stop.
        if (fork.isEmpty()) {
            graph.add(forkNode, stopNode);
            return stopNode;
        }

        for (int i = 0; i < fork.length(); i++) {
            JSONObject next = fork.getJSONObject(i);
            JSONArray nextComponents = Fork.next(next);

            GraphNode currentNode = forkNode;
            for (int j = 0; j < nextComponents.length(); j++) {
                JSONObject currentComponentDefinition = nextComponents.getJSONObject(j);
                currentNode = DeserializerFactory.get()
                        .componentDefinition(currentComponentDefinition)
                        .context(context)
                        .graph(graph)
                        .build()
                        .deserialize(currentNode, currentComponentDefinition);
            }

            graph.add(currentNode, stopNode);
        }

        // We must add all the nodes between fork and the stop node
        // to the fork's scope.
        CollectNodesBetween
                .them(graph, forkNode, stopNode)
                .forEach(forkNode::addToScope);

        return stopNode;
    }
}
