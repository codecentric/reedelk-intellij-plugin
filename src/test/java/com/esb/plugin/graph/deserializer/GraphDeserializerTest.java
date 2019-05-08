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
                .successorsOf(componentNode1)
                .areExactly(choiceNode)

                .and()
                .successorsOf(choiceNode)
                .areExactly(componentNode2, flowReferenceNode, forkNode)

                .and()
                .successorsOf(componentNode2)
                .areExactly(componentNode6)

                .and()
                .successorsOf(flowReferenceNode)
                .areExactly(componentNode6)

                .and()
                .successorsOf(forkNode)
                .areExactly(componentNode3, componentNode4)

                .and()
                .successorsOf(componentNode3)
                .areExactly(componentNode5)

                .and()
                .successorsOf(componentNode4)
                .areExactly(componentNode5)

                .and()
                .successorsOf(componentNode5)
                .areExactly(componentNode6);
    }

}