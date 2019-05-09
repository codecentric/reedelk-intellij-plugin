package com.esb.plugin.component.choice;

import com.esb.plugin.AbstractGraphTest;
import com.esb.plugin.component.ComponentData;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.FlowGraphImpl;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.util.ArrayList;
import java.util.List;

import static com.esb.plugin.component.choice.ChoiceNode.DATA_CONDITION_ROUTE_PAIRS;
import static com.esb.plugin.component.choice.ChoiceNode.DEFAULT_CONDITION_NAME;
import static com.esb.plugin.fixture.Json.Choice;

public class ChoiceSerializerTest extends AbstractGraphTest {

    private ChoiceSerializer serializer;

    @BeforeEach
    protected void setUp() {
        super.setUp();
        serializer = new ChoiceSerializer();
    }

    @Test
    void shouldCorrectlySerializeChoiceWithNodeOutsideScope() {
        // Given
        FlowGraph graph = new FlowGraphImpl();
        graph.root(root);
        graph.add(root, choiceNode1);

        graph.add(choiceNode1, componentNode3);
        graph.add(componentNode3, componentNode1);

        graph.add(choiceNode1, componentNode2);
        graph.add(componentNode2, componentNode4);

        graph.add(choiceNode1, componentNode5);
        graph.add(componentNode5, componentNode6);

        graph.add(componentNode1, componentNode7);
        graph.add(componentNode4, componentNode7);
        graph.add(componentNode6, componentNode7);

        choiceNode1.addToScope(componentNode1);
        choiceNode1.addToScope(componentNode2);
        choiceNode1.addToScope(componentNode3);
        choiceNode1.addToScope(componentNode4);
        choiceNode1.addToScope(componentNode5);
        choiceNode1.addToScope(componentNode6);

        List<ChoiceConditionRoutePair> choiceRoute = new ArrayList<>();
        choiceRoute.add(new ChoiceConditionRoutePair(DEFAULT_CONDITION_NAME, componentNode5));
        choiceRoute.add(new ChoiceConditionRoutePair("1 == 1", componentNode3));
        choiceRoute.add(new ChoiceConditionRoutePair("1 != 0", componentNode2));

        ComponentData component = choiceNode1.componentData();
        component.set(DATA_CONDITION_ROUTE_PAIRS, choiceRoute);

        // When
        JSONObject serializedObject = serializer.serialize(graph, choiceNode1, componentNode7);

        // Then
        String actualJson = serializedObject.toString(2);
        String expectedJson = Choice.Sample.json();
        JSONAssert.assertEquals(expectedJson, actualJson, true);
    }

    @Test
    void shouldCorrectlySerializeChoiceWithoutNodeOutsideScope() {
        // Given
        FlowGraph graph = new FlowGraphImpl();
        graph.root(root);
        graph.add(root, choiceNode1);

        graph.add(choiceNode1, componentNode3);
        graph.add(componentNode3, componentNode1);

        graph.add(choiceNode1, componentNode2);
        graph.add(componentNode2, componentNode4);

        graph.add(choiceNode1, componentNode5);
        graph.add(componentNode5, componentNode6);

        choiceNode1.addToScope(componentNode1);
        choiceNode1.addToScope(componentNode2);
        choiceNode1.addToScope(componentNode3);
        choiceNode1.addToScope(componentNode4);
        choiceNode1.addToScope(componentNode5);
        choiceNode1.addToScope(componentNode6);

        List<ChoiceConditionRoutePair> choiceRoute = new ArrayList<>();
        choiceRoute.add(new ChoiceConditionRoutePair(DEFAULT_CONDITION_NAME, componentNode5));
        choiceRoute.add(new ChoiceConditionRoutePair("1 == 1", componentNode3));
        choiceRoute.add(new ChoiceConditionRoutePair("1 != 0", componentNode2));

        ComponentData component = choiceNode1.componentData();
        component.set(DATA_CONDITION_ROUTE_PAIRS, choiceRoute);

        // When
        JSONObject serializedObject = serializer.serialize(graph, choiceNode1, componentNode7);

        // Then
        String actualJson = serializedObject.toString(2);
        String expectedJson = Choice.Sample.json();
        JSONAssert.assertEquals(expectedJson, actualJson, true);
    }

}
