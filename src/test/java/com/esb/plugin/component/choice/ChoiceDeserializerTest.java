package com.esb.plugin.component.choice;

import com.esb.plugin.AbstractDeserializerTest;
import com.esb.plugin.assertion.PluginAssertion;
import com.esb.plugin.component.ComponentData;
import com.esb.plugin.graph.node.GraphNode;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;

import static com.esb.plugin.fixture.Json.Choice;
import static org.assertj.core.api.Assertions.assertThat;

@MockitoSettings(strictness = Strictness.LENIENT)
class ChoiceDeserializerTest extends AbstractDeserializerTest {

    private ChoiceDeserializer deserializer;

    @BeforeEach
    protected void setUp() {
        super.setUp();
        deserializer = new ChoiceDeserializer(graph, context);
    }

    @Test
    void shouldDeserializeChoiceDefinitionCorrectly() {
        // Given
        JSONObject choiceDefinition = new JSONObject(Choice.Sample.json());

        // When
        GraphNode lastNode = deserializer.deserialize(root, choiceDefinition);

        // Then
        PluginAssertion.assertThat(graph)
                .node(lastNode).is(stopNode1)
                .and().successorsOf(choiceNode1).areExactly(componentNode3, componentNode2, componentNode5)
                .and().successorsOf(componentNode3).isOnly(componentNode1)
                .and().successorsOf(componentNode2).isOnly(componentNode4)
                .and().successorsOf(componentNode5).isOnly(componentNode6)
                .and().predecessorOf(lastNode).containsExactly(componentNode1, componentNode4, componentNode6)
                .and().nodesCountIs(9); // total nodes include: root, stop node and all the nodes belonging to this choice

        assertExistsConditionMatching("1 == 1", choiceNode1);
        assertExistsConditionMatching("1 != 0", choiceNode1);
        assertExistsConditionMatching("otherwise", choiceNode1);
    }

    private void assertExistsConditionMatching(String expectedCondition, ChoiceNode targetNode) {
        // TODO: Fix this cast!
        ComponentData componentData = targetNode.componentData();
        List<ChoiceConditionRoutePair> when = (List<ChoiceConditionRoutePair>) componentData.get("conditionRoutePairs");
        boolean matchesCondition = when.stream().anyMatch(choiceConditionRoutePair ->
                choiceConditionRoutePair.getCondition().equals(expectedCondition));
        assertThat(matchesCondition).isTrue();
    }
}
