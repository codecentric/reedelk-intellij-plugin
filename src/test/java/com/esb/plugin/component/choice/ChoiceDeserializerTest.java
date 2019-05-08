package com.esb.plugin.component.choice;

import com.esb.plugin.AbstractDeserializerTest;
import com.esb.plugin.assertion.PluginAssertion;
import com.esb.plugin.component.stop.StopNode;
import com.esb.plugin.fixture.*;
import com.esb.plugin.graph.node.GraphNode;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ChoiceDeserializerTest extends AbstractDeserializerTest {

    private ChoiceDeserializer deserializer;

    private StopNode stopNode;
    private ChoiceNode choiceNode;

    private GraphNode componentNode1;
    private GraphNode componentNode2;
    private GraphNode componentNode3;
    private GraphNode componentNode4;
    private GraphNode componentNode5;
    private GraphNode componentNode6;

    @BeforeEach
    protected void setUp() {
        super.setUp();
        deserializer = new ChoiceDeserializer(graph, context);

        stopNode = mockStopNode();
        choiceNode = mockChoiceNode();
        componentNode1 = mockGenericComponentNode(ComponentNode1.class);
        componentNode2 = mockGenericComponentNode(ComponentNode2.class);
        componentNode3 = mockGenericComponentNode(ComponentNode3.class);
        componentNode4 = mockGenericComponentNode(ComponentNode4.class);
        componentNode5 = mockGenericComponentNode(ComponentNode5.class);
        componentNode6 = mockGenericComponentNode(ComponentNode6.class);
    }

    @Test
    void shouldDeserializeChoiceDefinitionCorrectly() {
        // Given
        JSONObject choiceDefinition = new JSONObject(Json.Choice.Sample.asJson());

        // When
        GraphNode lastNode = deserializer.deserialize(root, choiceDefinition);

        // Then: last node must be a stop node
        assertThat(lastNode).isEqualTo(stopNode);

        // Then: check successors of choice
        PluginAssertion.assertThat(graph)

                .node(lastNode)
                .isEqualTo(stopNode)

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

                .and()
                .nodesCountIs(9);
    }

}
