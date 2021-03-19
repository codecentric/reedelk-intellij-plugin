package de.codecentric.reedelk.plugin.graph.serializer;

import de.codecentric.reedelk.plugin.AbstractGraphTest;
import de.codecentric.reedelk.plugin.fixture.Json;
import de.codecentric.reedelk.plugin.component.type.router.RouterConditionRoutePair;
import de.codecentric.reedelk.plugin.component.type.router.RouterNode;
import de.codecentric.reedelk.plugin.graph.FlowGraph;
import de.codecentric.reedelk.runtime.component.Router;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.util.ArrayList;
import java.util.List;

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
        routerNode1Conditions.add(new RouterConditionRoutePair(Router.DEFAULT_CONDITION.value(), flowReferenceNode1));
        routerNode1.componentData().set(RouterNode.DATA_CONDITION_ROUTE_PAIRS, routerNode1Conditions);
        flowReferenceNode1.componentData().set("ref", "dee1d69e-e749-4cd5-9134-8f8615be1d40");

        routerNode2.addToScope(flowReferenceNode2);
        List<RouterConditionRoutePair> routerNode2Conditions = new ArrayList<>();
        routerNode2Conditions.add(new RouterConditionRoutePair(Router.DEFAULT_CONDITION.value(), flowReferenceNode2));
        routerNode2.componentData().set(RouterNode.DATA_CONDITION_ROUTE_PAIRS, routerNode2Conditions);
        flowReferenceNode2.componentData().set("ref", "e2a81295-c2eb-48b2-a2b1-6b1e740cdd24");

        // When
        String actualJson = FlowSerializer.serialize(graph);

        // Then
        String expectedJson = Json.CompleteFlow.NestedRouter.json();
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
        routerNode1Conditions.add(new RouterConditionRoutePair(Router.DEFAULT_CONDITION.value(), flowReferenceNode1));
        routerNode1.componentData().set(RouterNode.DATA_CONDITION_ROUTE_PAIRS, routerNode1Conditions);
        flowReferenceNode1.componentData().set("ref", "dee1d69e-e749-4cd5-9134-8f8615be1d40");

        routerNode2.addToScope(flowReferenceNode2);
        List<RouterConditionRoutePair> routerNode2Conditions = new ArrayList<>();
        routerNode2Conditions.add(new RouterConditionRoutePair(Router.DEFAULT_CONDITION.value(), flowReferenceNode2));
        routerNode2.componentData().set(RouterNode.DATA_CONDITION_ROUTE_PAIRS, routerNode2Conditions);
        flowReferenceNode2.componentData().set("ref", "e2a81295-c2eb-48b2-a2b1-6b1e740cdd24");

        // When
        String actualJson = FlowSerializer.serialize(graph);

        // Then
        String expectedJson = Json.CompleteFlow.NodesBetweenScopes.json();
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

        // When
        String actualJson = FlowSerializer.serialize(graph);

        // Then
        String expectedJson = Json.Fork.WithoutSuccessorInsideScope.json();
        JSONAssert.assertEquals(expectedJson, actualJson, true);
    }
}
