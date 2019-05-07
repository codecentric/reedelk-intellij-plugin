package com.esb.plugin.component.choice;

import com.esb.plugin.TestJson;
import com.esb.plugin.component.ComponentData;
import com.esb.plugin.graph.AbstractGraphTest;
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
        graph.add(root, choice1);

        graph.add(choice1, n1);
        graph.add(n1, n2);

        graph.add(choice1, n3);
        graph.add(n3, n4);

        graph.add(choice1, n5);
        graph.add(n5, n6);

        graph.add(n2, n7);
        graph.add(n4, n7);
        graph.add(n6, n7);

        choice1.addToScope(n1);
        choice1.addToScope(n2);
        choice1.addToScope(n3);
        choice1.addToScope(n4);
        choice1.addToScope(n5);
        choice1.addToScope(n6);

        ComponentData component = choice1.component();
        List<ChoiceConditionRoutePair> choiceRoute = new ArrayList<>();
        choiceRoute.add(new ChoiceConditionRoutePair(DEFAULT_CONDITION_NAME, n1));
        choiceRoute.add(new ChoiceConditionRoutePair("payload == 'Mark'", n3));
        choiceRoute.add(new ChoiceConditionRoutePair("payload == 'John'", n5));
        component.set(DATA_CONDITION_ROUTE_PAIRS, choiceRoute);

        // When
        JSONObject serializedObject = serializer.serialize(graph, choice1, n7);

        // Then
        String actualJson = serializedObject.toString(2);
        String expectedJson = TestJson.CHOICE_WITH_NODE_OUTSIDE_SCOPE.asJson();
        JSONAssert.assertEquals(expectedJson, actualJson, true);
    }

}