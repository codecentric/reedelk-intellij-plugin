package com.reedelk.plugin.component.type.router;

import com.reedelk.component.descriptor.ComponentData;
import com.reedelk.plugin.component.type.stop.StopNode;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.deserializer.AbstractNodeDeserializer;
import com.reedelk.plugin.graph.deserializer.DeserializerContext;
import com.reedelk.plugin.graph.deserializer.DeserializerFactory;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.graph.utils.CollectNodesBetween;
import com.reedelk.plugin.graph.utils.FindRelatedScopeOfStopNode;
import com.reedelk.runtime.component.Stop;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.reedelk.plugin.component.type.router.RouterNode.DATA_CONDITION_ROUTE_PAIRS;
import static com.reedelk.runtime.commons.JsonParser.Implementor;
import static com.reedelk.runtime.commons.JsonParser.Router;

public class RouterDeserializer extends AbstractNodeDeserializer {

    public RouterDeserializer(FlowGraph graph, DeserializerContext context) {
        super(graph, context);
    }

    @Override
    public GraphNode deserialize(GraphNode parent, JSONObject componentDefinition) {

        StopNode stopNode = context.instantiateGraphNode(Stop.class.getName());

        String name = Implementor.name(componentDefinition);

        RouterNode routerNode = context.instantiateGraphNode(name);

        Map<GraphNode, String> nodeAndConditionMap = new LinkedHashMap<>();

        graph.add(parent, routerNode);

        // When
        JSONArray when = Router.when(componentDefinition);

        for (int i = 0; i < when.length(); i++) {

            JSONObject whenComponent = when.getJSONObject(i);

            JSONArray next = Router.next(whenComponent);

            GraphNode currentNode = deserialize(next, routerNode, node -> {
                // If the next contains the definition of a scoped drawable,
                // then the returned node from the deserializer returns a stop node.
                // We must find the the scope the stop node belongs to.
                GraphNode firstNonStopNode = findSuccessor(graph, node);
                String condition = Router.condition(whenComponent);
                nodeAndConditionMap.put(firstNonStopNode, condition);
            });

            // Last node is connected to stop node.
            graph.add(currentNode, stopNode);
        }

        // Add all the nodes just added (between router and stop node) to the Route's scope.
        CollectNodesBetween
                .them(graph, routerNode, stopNode)
                .forEach(routerNode::addToScope);

        ComponentData routerData = routerNode.componentData();

        // Set description
        if (componentDefinition.has(Implementor.description())) {
            routerData.set(Implementor.description(), Implementor.description(componentDefinition));
        }

        // Build Condition -> Route pairs list
        List<RouterConditionRoutePair> routerConditionRoutePairList = new ArrayList<>();
        routerData.set(DATA_CONDITION_ROUTE_PAIRS, routerConditionRoutePairList);

        for (Map.Entry<GraphNode, String> entry : nodeAndConditionMap.entrySet()) {
            RouterConditionRoutePair pair =
                    new RouterConditionRoutePair(entry.getValue(), entry.getKey());
            routerConditionRoutePairList.add(pair);
        }

        return stopNode;
    }

    /**
     * If the target is a stop node, it returns the ScopedGraphNode this stop
     * is referring to, otherwise the target node.
     */
    private GraphNode findSuccessor(FlowGraph graph, GraphNode target) {
        return target instanceof StopNode ?
                FindRelatedScopeOfStopNode.find(graph, (StopNode) target) : target;
    }

    private interface Action {
        void perform(GraphNode node);
    }

    private GraphNode deserialize(JSONArray arrayToDeserialize, GraphNode parent, Action actionOnFirst) {
        GraphNode currentNode = parent;
        for (int i = 0; i < arrayToDeserialize.length(); i++) {
            JSONObject currentComponentDef = arrayToDeserialize.getJSONObject(i);
            currentNode = DeserializerFactory.get()
                    .componentDefinition(currentComponentDef)
                    .context(context)
                    .graph(graph)
                    .build()
                    .deserialize(currentNode, currentComponentDef);

            if (i == 0) {
                actionOnFirst.perform(currentNode);
            }
        }
        return currentNode;
    }
}
