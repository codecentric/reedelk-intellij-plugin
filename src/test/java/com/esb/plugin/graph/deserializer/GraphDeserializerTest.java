package com.esb.plugin.graph.deserializer;

import com.esb.plugin.AbstractDeserializerTest;
import com.esb.plugin.assertion.PluginAssertion;
import com.esb.plugin.graph.FlowGraph;
import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static com.esb.plugin.fixture.Json.CompleteFlow;

@MockitoSettings(strictness = Strictness.LENIENT)
class GraphDeserializerTest extends AbstractDeserializerTest {

    @Test
    void shouldDeserializeFlowWithAllDefaultComponentsCorrectly() {
        // Given
        String json = CompleteFlow.Sample.json();
        GraphDeserializer deserializer = new GraphDeserializer(json, context);

        // When
        FlowGraph graph = deserializer.deserialize();

        // Then
        PluginAssertion.assertThat(graph)

                .node(graph.root())
                .is(componentNode1)

                .and()
                .successorsOf(componentNode1)
                .areExactly(choiceNode1)

                .and()
                .successorsOf(choiceNode1)
                .areExactly(componentNode2, flowReferenceNode1, forkNode)

                .and()
                .successorsOf(componentNode2)
                .areExactly(componentNode6)

                .and()
                .successorsOf(flowReferenceNode1)
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

    @Test
    void shouldDeserializeFlowWithNestedChoiceCorrectly() {
        // Given
        String json = CompleteFlow.NestedChoice.json();
        GraphDeserializer deserializer = new GraphDeserializer(json, context);

        // When
        FlowGraph graph = deserializer.deserialize();

        // Then
        PluginAssertion.assertThat(graph)

                .node(graph.root())
                .is(componentNode1);
    }

}