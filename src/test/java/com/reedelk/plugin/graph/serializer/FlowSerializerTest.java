package com.reedelk.plugin.graph.serializer;

import com.reedelk.plugin.AbstractGraphTest;
import com.reedelk.plugin.component.domain.ComponentData;
import com.reedelk.plugin.component.type.router.RouterConditionRoutePair;
import com.reedelk.plugin.component.type.router.RouterNode;
import com.reedelk.plugin.fixture.Json;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.runtime.commons.JsonParser;
import com.reedelk.runtime.component.Router;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.util.ArrayList;
import java.util.List;

import static com.reedelk.plugin.fixture.Json.CompleteFlow;

class FlowSerializerTest extends AbstractGraphTest {

    @Test
    void shouldCorrectlySerializeGraphWithNestedRouter() {
        // Given
        String expectedGraphId = "d7f8160c-db7f-405c-bc38-bd2e3c57b692";
        FlowGraph graph = provider.createGraph(expectedGraphId);
        graph.root(componentNode1);
        graph.add(componentNode1, routerNode1);
        graph.add(routerNode1, flowReferenceNode1);
        graph.add(flowReferenceNode1, routerNode2);
        graph.add(routerNode2, flowReferenceNode2);
        graph.add(flowReferenceNode2, componentNode2);

        routerNode1.addToScope(flowReferenceNode1);
        routerNode1.addToScope(routerNode2);

        List<RouterConditionRoutePair> routerNode1Conditions = new ArrayList<>();
        routerNode1Conditions.add(new RouterConditionRoutePair(Router.DEFAULT_CONDITION.getValue(), flowReferenceNode1));
        routerNode1.componentData().set(RouterNode.DATA_CONDITION_ROUTE_PAIRS, routerNode1Conditions);
        flowReferenceNode1.componentData().set("ref", "dee1d69e-e749-4cd5-9134-8f8615be1d40");

        routerNode2.addToScope(flowReferenceNode2);
        List<RouterConditionRoutePair> routerNode2Conditions = new ArrayList<>();
        routerNode2Conditions.add(new RouterConditionRoutePair(Router.DEFAULT_CONDITION.getValue(), flowReferenceNode2));
        routerNode2.componentData().set(RouterNode.DATA_CONDITION_ROUTE_PAIRS, routerNode2Conditions);
        flowReferenceNode2.componentData().set("ref", "e2a81295-c2eb-48b2-a2b1-6b1e740cdd24");

        // When
        String actualJson = FlowSerializer.serialize(graph);

        // Then
        String expectedJson = CompleteFlow.NestedRouter.json();
        JSONAssert.assertEquals(expectedJson, actualJson, true);
    }

    @Test
    void shouldCorrectlySerializeGraphWithNodesBetweenScopes() {
        // Given
        String expectedId = "d7f8160c-db7f-405c-bc38-bd2e3c57b692";
        FlowGraph graph = provider.createGraph(expectedId);
        graph.root(componentNode1);
        graph.add(componentNode1, routerNode1);
        graph.add(routerNode1, flowReferenceNode1);
        graph.add(flowReferenceNode1, routerNode2);
        graph.add(routerNode2, flowReferenceNode2);
        graph.add(flowReferenceNode2, componentNode2);
        graph.add(componentNode2, componentNode3);

        routerNode1.addToScope(flowReferenceNode1);
        routerNode1.addToScope(routerNode2);
        routerNode1.addToScope(componentNode2);

        List<RouterConditionRoutePair> routerNode1Conditions = new ArrayList<>();
        routerNode1Conditions.add(new RouterConditionRoutePair(Router.DEFAULT_CONDITION.getValue(), flowReferenceNode1));
        routerNode1.componentData().set(RouterNode.DATA_CONDITION_ROUTE_PAIRS, routerNode1Conditions);
        flowReferenceNode1.componentData().set("ref", "dee1d69e-e749-4cd5-9134-8f8615be1d40");

        routerNode2.addToScope(flowReferenceNode2);
        List<RouterConditionRoutePair> routerNode2Conditions = new ArrayList<>();
        routerNode2Conditions.add(new RouterConditionRoutePair(Router.DEFAULT_CONDITION.getValue(), flowReferenceNode2));
        routerNode2.componentData().set(RouterNode.DATA_CONDITION_ROUTE_PAIRS, routerNode2Conditions);
        flowReferenceNode2.componentData().set("ref", "e2a81295-c2eb-48b2-a2b1-6b1e740cdd24");

        // When
        String actualJson = FlowSerializer.serialize(graph);

        // Then
        String expectedJson = CompleteFlow.NodesBetweenScopes.json();
        JSONAssert.assertEquals(expectedJson, actualJson, true);
    }

    @Test
    void shouldCorrectlySerializeForkWithoutSuccessorInsideScope() {
        // Given
        String expectedId = "d7f8160c-db7f-405c-bc38-bd2e3c57b692";
        FlowGraph graph = provider.createGraph(expectedId);
        graph.root(root);
        graph.add(root, forkNode1);
        graph.add(forkNode1, componentNode1);

        ComponentData componentData = forkNode1.componentData();
        componentData.set(JsonParser.Fork.threadPoolSize(), 3);

        // When
        String actualJson = FlowSerializer.serialize(graph);

        // Then
        String expectedJson = Json.Fork.WithoutSuccessorInsideScope.json();
        JSONAssert.assertEquals(expectedJson, actualJson, true);
    }
}
