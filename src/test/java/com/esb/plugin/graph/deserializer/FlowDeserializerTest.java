package com.esb.plugin.graph.deserializer;

import com.esb.plugin.AbstractNodeDeserializerTest;
import com.esb.plugin.assertion.PluginAssertion;
import com.esb.plugin.graph.FlowGraph;
import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static com.esb.plugin.fixture.Json.CompleteFlow;

@MockitoSettings(strictness = Strictness.LENIENT)
class FlowDeserializerTest extends AbstractNodeDeserializerTest {

    @Test
    void shouldDeserializeFlowWithAllDefaultComponentsCorrectly() {
        // Given
        String json = CompleteFlow.Sample.json();
        FlowDeserializer deserializer = new FlowDeserializer(json, context, provider);

        // When
        FlowGraph graph = deserializer.deserialize();

        // Then
        PluginAssertion.assertThat(graph)
                .root().is(componentNode1)
                .and().successorsOf(componentNode1).isOnly(choiceNode1)
                .and().successorsOf(choiceNode1).areExactly(componentNode2, flowReferenceNode1, forkNode1)
                .and().successorsOf(componentNode2).isOnly(componentNode5)
                .and().successorsOf(flowReferenceNode1).isOnly(componentNode5)
                .and().successorsOf(forkNode1).areExactly(componentNode3, componentNode4)
                .and().successorsOf(componentNode3).isOnly(componentNode5)
                .and().successorsOf(componentNode4).isOnly(componentNode5);
    }

    @Test
    void shouldDeserializeFlowWithNestedChoiceCorrectly() {
        // Given
        String json = CompleteFlow.NestedChoice.json();
        FlowDeserializer deserializer = new FlowDeserializer(json, context, provider);

        // When
        FlowGraph graph = deserializer.deserialize();

        // Then
        PluginAssertion.assertThat(graph)
                .root().is(componentNode1)
                .and().successorsOf(componentNode1).isOnly(choiceNode1)
                .and().successorsOf(choiceNode1).isOnly(flowReferenceNode1)
                .and().successorsOf(flowReferenceNode1).isOnly(choiceNode2)
                .and().successorsOf(choiceNode2).isOnly(flowReferenceNode2)
                .and().successorsOf(flowReferenceNode2).isOnly(componentNode2)
                .and().node(choiceNode1).scopeContainsExactly(flowReferenceNode1, choiceNode2)
                .and().node(choiceNode2).scopeContainsExactly(flowReferenceNode2);
    }

    @Test
    void shouldDeserializeFlowWithNestedForkCorrectly() {
        // Given
        String json = CompleteFlow.NestedFork.json();
        FlowDeserializer deserializer = new FlowDeserializer(json, context, provider);

        // When
        FlowGraph graph = deserializer.deserialize();

        // Then
        PluginAssertion.assertThat(graph)
                .root().is(componentNode1)
                .and().successorsOf(forkNode1).areExactly(forkNode2, forkNode3)
                .and().successorsOf(forkNode2).isOnly(componentNode2)
                .and().node(forkNode2).scopeContainsExactly(componentNode2)
                .and().successorsOf(forkNode3).isOnly(componentNode3)
                .and().node(forkNode3).scopeContainsExactly(componentNode3)
                .and().successorsOf(componentNode2).isOnly(componentNode4)
                .and().successorsOf(componentNode3).isOnly(componentNode4)
                .and().successorsOf(componentNode4).isEmpty();
    }

    @Test
    void shouldDeserializeFlowWithEmptyForkCorrectly() {
        // Given
        String json = CompleteFlow.NestedEmptyFork.json();
        FlowDeserializer deserializer = new FlowDeserializer(json, context, provider);

        // When
        FlowGraph graph = deserializer.deserialize();

        // Then
        PluginAssertion.assertThat(graph)
                .root().is(componentNode1)
                .and().successorsOf(componentNode1).isOnly(forkNode1)
                .and().successorsOf(forkNode1).isOnly(componentNode2)
                .and().node(forkNode1).scopeIsEmpty()
                .and().successorsOf(componentNode2).isEmpty();
    }
}