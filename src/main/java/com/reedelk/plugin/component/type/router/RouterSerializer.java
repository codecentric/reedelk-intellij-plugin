package com.reedelk.plugin.component.type.router;

import com.reedelk.component.descriptor.ComponentData;
import com.reedelk.plugin.commons.JsonObjectFactory;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.graph.node.ScopedGraphNode;
import com.reedelk.plugin.graph.serializer.AbstractScopedNodeSerializer;
import com.reedelk.plugin.graph.serializer.SerializerFactory;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import static com.reedelk.runtime.commons.JsonParser.Implementor;
import static com.reedelk.runtime.commons.JsonParser.Router;

public class RouterSerializer extends AbstractScopedNodeSerializer {

    @Override
    protected JSONObject serializeScopedNode(FlowGraph graph, ScopedGraphNode routerNode, GraphNode stop) {
        ComponentData componentData = routerNode.componentData();

        JSONObject routerObject = JsonObjectFactory.newJSONObject();

        Implementor.name(componentData.getFullyQualifiedName(), routerObject);

        String description = componentData.get(Implementor.description());

        Implementor.description(description, routerObject);

        List<RouterConditionRoutePair> conditionRoutePairs = componentData.get(RouterNode.DATA_CONDITION_ROUTE_PAIRS);

        JSONArray whenArrayObject = new JSONArray();

        List<GraphNode> routerSuccessors = graph.successors(routerNode);

        // Invert, we should first get the successors of the router and then find the matching PAIR
        for (GraphNode routeSuccessor : routerSuccessors) {

            RouterConditionRoutePair pair = findPairFor(routeSuccessor, conditionRoutePairs);

            JSONObject conditionAndRouteObject = JsonObjectFactory.newJSONObject();

            String condition = pair.getCondition();

            Router.condition(condition, conditionAndRouteObject);

            JSONArray nextArrayObject = new JSONArray();

            GraphNode nextNode = pair.getNext();

            SerializerFactory.get()
                    .node(nextNode)
                    .build()
                    .serialize(graph, nextArrayObject, nextNode, stop);

            Router.next(nextArrayObject, conditionAndRouteObject);

            whenArrayObject.put(conditionAndRouteObject);
        }

        Router.when(whenArrayObject, routerObject);

        return routerObject;
    }

    private RouterConditionRoutePair findPairFor(GraphNode routerNode, List<RouterConditionRoutePair> conditionRoutePairs) {
        return conditionRoutePairs.stream()
                .filter(routerConditionRoutePair ->
                        routerConditionRoutePair.getNext() == routerNode)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Router condition route pair must be present."));
    }

}
