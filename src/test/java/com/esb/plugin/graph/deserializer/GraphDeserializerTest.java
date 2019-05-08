package com.esb.plugin.graph.deserializer;

import com.esb.plugin.AbstractDeserializerTest;
import com.esb.plugin.assertion.PluginAssertion;
import com.esb.plugin.fixture.Json;
import com.esb.plugin.graph.FlowGraph;
import org.junit.jupiter.api.Test;

class GraphDeserializerTest extends AbstractDeserializerTest {

    @Test
    void shouldDeserializeFlowWithAllDefaultComponentsCorrectly() {
        // Given
        String json = Json.CompleteFlow.Sample.json();
        GraphDeserializer deserializer = new GraphDeserializer(json, context);

        // When
        FlowGraph graph = deserializer.deserialize();

        // Then
        PluginAssertion.assertThat(graph)

                .node(graph.root())
                .is(componentNode1)

                .and()
                .successorOf(componentNode1)
                .containsExactly(choiceNode)

                .and()
                .successorOf(choiceNode)
                .containsExactly(componentNode2, flowReferenceNode, forkNode)

                .and()
                .successorOf(componentNode2)
                .containsExactly(componentNode6)

                .and()
                .successorOf(flowReferenceNode)
                .containsExactly(componentNode6)

                .and()
                .successorOf(forkNode)
                .containsExactly(componentNode3, componentNode4)

                .and()
                .successorOf(componentNode3)
                .containsExactly(componentNode5)

                .and()
                .successorOf(componentNode4)
                .containsExactly(componentNode5)

                .and()
                .successorOf(componentNode5)
                .containsExactly(componentNode6);
    }

}