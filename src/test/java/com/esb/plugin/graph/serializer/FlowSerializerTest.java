package com.esb.plugin.graph.serializer;

import com.esb.internal.commons.JsonParser;
import com.esb.plugin.AbstractGraphTest;
import com.esb.plugin.component.domain.ComponentData;
import com.esb.plugin.component.type.choice.ChoiceConditionRoutePair;
import com.esb.plugin.component.type.choice.ChoiceNode;
import com.esb.plugin.fixture.Json;
import com.esb.plugin.graph.FlowGraph;
import com.esb.system.component.Choice;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.util.ArrayList;
import java.util.List;

import static com.esb.plugin.fixture.Json.CompleteFlow;

class FlowSerializerTest extends AbstractGraphTest {

    @Test
    void shouldCorrectlySerializeGraphWithNestedChoice() {
        // Given
        String expectedGraphId = "d7f8160c-db7f-405c-bc38-bd2e3c57b692";
        FlowGraph graph = provider.createGraph(expectedGraphId);
        graph.root(componentNode1);
        graph.add(componentNode1, choiceNode1);
        graph.add(choiceNode1, flowReferenceNode1);
        graph.add(flowReferenceNode1, choiceNode2);
        graph.add(choiceNode2, flowReferenceNode2);
        graph.add(flowReferenceNode2, componentNode2);

        choiceNode1.addToScope(flowReferenceNode1);
        choiceNode1.addToScope(choiceNode2);

        List<ChoiceConditionRoutePair> choiceNode1Conditions = new ArrayList<>();
        choiceNode1Conditions.add(new ChoiceConditionRoutePair(Choice.DEFAULT_CONDITION, flowReferenceNode1));
        choiceNode1.componentData().set(ChoiceNode.DATA_CONDITION_ROUTE_PAIRS, choiceNode1Conditions);
        flowReferenceNode1.componentData().set("ref", "dee1d69e-e749-4cd5-9134-8f8615be1d40");

        choiceNode2.addToScope(flowReferenceNode2);
        List<ChoiceConditionRoutePair> choiceNode2Conditions = new ArrayList<>();
        choiceNode2Conditions.add(new ChoiceConditionRoutePair(Choice.DEFAULT_CONDITION, flowReferenceNode2));
        choiceNode2.componentData().set(ChoiceNode.DATA_CONDITION_ROUTE_PAIRS, choiceNode2Conditions);
        flowReferenceNode2.componentData().set("ref", "e2a81295-c2eb-48b2-a2b1-6b1e740cdd24");

        // When
        String actualJson = FlowSerializer.serialize(graph);

        // Then
        String expectedJson = CompleteFlow.NestedChoice.json();
        JSONAssert.assertEquals(expectedJson, actualJson, true);
    }

    @Test
    void shouldCorrectlySerializeGraphWithNodesBetweenScopes() {
        // Given
        String expectedId = "d7f8160c-db7f-405c-bc38-bd2e3c57b692";
        FlowGraph graph = provider.createGraph(expectedId);
        graph.root(componentNode1);
        graph.add(componentNode1, choiceNode1);
        graph.add(choiceNode1, flowReferenceNode1);
        graph.add(flowReferenceNode1, choiceNode2);
        graph.add(choiceNode2, flowReferenceNode2);
        graph.add(flowReferenceNode2, componentNode2);
        graph.add(componentNode2, componentNode3);

        choiceNode1.addToScope(flowReferenceNode1);
        choiceNode1.addToScope(choiceNode2);
        choiceNode1.addToScope(componentNode2);

        List<ChoiceConditionRoutePair> choiceNode1Conditions = new ArrayList<>();
        choiceNode1Conditions.add(new ChoiceConditionRoutePair(Choice.DEFAULT_CONDITION, flowReferenceNode1));
        choiceNode1.componentData().set(ChoiceNode.DATA_CONDITION_ROUTE_PAIRS, choiceNode1Conditions);
        flowReferenceNode1.componentData().set("ref", "dee1d69e-e749-4cd5-9134-8f8615be1d40");

        choiceNode2.addToScope(flowReferenceNode2);
        List<ChoiceConditionRoutePair> choiceNode2Conditions = new ArrayList<>();
        choiceNode2Conditions.add(new ChoiceConditionRoutePair(Choice.DEFAULT_CONDITION, flowReferenceNode2));
        choiceNode2.componentData().set(ChoiceNode.DATA_CONDITION_ROUTE_PAIRS, choiceNode2Conditions);
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
