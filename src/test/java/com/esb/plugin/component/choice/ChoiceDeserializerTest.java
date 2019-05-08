package com.esb.plugin.component.choice;

import com.esb.plugin.AbstractDeserializerTest;
import com.esb.plugin.assertion.PluginAssertion;
import com.esb.plugin.graph.node.GraphNode;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static com.esb.plugin.fixture.Json.Choice;

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

                .node(lastNode)
                .is(stopNode1)

                .and()
                .successorOf(choiceNode)
                .containsExactly(componentNode3, componentNode2, componentNode5)

                .and()
                .successorOf(componentNode3)
                .containsExactly(componentNode1)

                .and()
                .successorOf(componentNode2)
                .containsExactly(componentNode4)

                .and()
                .successorOf(componentNode5)
                .containsExactly(componentNode6)

                .and()
                .predecessorOf(lastNode)
                .containsExactly(componentNode1, componentNode4, componentNode6)

                // total nodes include: root, stop node and all the nodes belonging to this choice
                .and()
                .nodesCountIs(9);
    }
}
