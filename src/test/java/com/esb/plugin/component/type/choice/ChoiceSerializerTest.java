package com.esb.plugin.component.type.choice;

import com.esb.plugin.AbstractGraphTest;
import com.esb.plugin.component.domain.ComponentData;
import com.esb.plugin.graph.FlowGraph;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.util.List;

import static com.esb.plugin.component.type.choice.ChoiceNode.DATA_CONDITION_ROUTE_PAIRS;
import static com.esb.plugin.component.type.choice.ChoiceNode.DEFAULT_CONDITION_NAME;
import static com.esb.plugin.fixture.Json.Choice;
import static java.util.Arrays.asList;

public class ChoiceSerializerTest extends AbstractGraphTest {

    private ChoiceSerializer serializer;

    @BeforeEach
    protected void setUp() {
        super.setUp();
        serializer = new ChoiceSerializer();
    }

    @Test
    void shouldCorrectlySerializeChoiceNode() {
        // Given
        JSONArray sequence = new JSONArray();

        FlowGraph graph = graphProvider.createGraph();
        graph.root(root);
        graph.add(root, choiceNode1);

        graph.add(choiceNode1, componentNode3);
        graph.add(choiceNode1, componentNode2);
        graph.add(choiceNode1, componentNode5);

        graph.add(componentNode3, componentNode1);
        graph.add(componentNode2, componentNode4);
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

        List<ChoiceConditionRoutePair> choiceRoute = asList(
                new ChoiceConditionRoutePair("1 == 1", componentNode3),
                new ChoiceConditionRoutePair("1 != 0", componentNode2),
                new ChoiceConditionRoutePair(DEFAULT_CONDITION_NAME, componentNode5));

        ComponentData component = choiceNode1.componentData();
        component.set(DATA_CONDITION_ROUTE_PAIRS, choiceRoute);

        // When
        serializer.serialize(graph, sequence, choiceNode1, componentNode7);

        // Then
        JSONObject serializedObject = sequence.getJSONObject(0);

        String actualJson = serializedObject.toString(2);
        String expectedJson = Choice.Sample.json();
        JSONAssert.assertEquals(expectedJson, actualJson, true);
    }

}
