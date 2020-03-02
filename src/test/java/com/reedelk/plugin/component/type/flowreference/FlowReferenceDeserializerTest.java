package com.reedelk.plugin.component.type.flowreference;

import com.reedelk.plugin.assertion.PluginAssertion;
import com.reedelk.plugin.graph.deserializer.AbstractNodeDeserializerTest;
import com.reedelk.plugin.graph.node.GraphNode;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.reedelk.plugin.fixture.Json.FlowReference;

class FlowReferenceDeserializerTest extends AbstractNodeDeserializerTest {

    private FlowReferenceDeserializer deserializer;

    @BeforeEach
    protected void setUp() {
        super.setUp();
        deserializer = new FlowReferenceDeserializer(graph, flowReferenceNode1, context);
    }

    @Test
    void shouldDeserializeFlowReferenceDefinitionCorrectly() {
        // Given
        String expectedFlowReference = "11a2ce60-5c9d-1111-82a7-f1fa1111f333";

        JSONObject flowReferenceDefinition = new JSONObject(FlowReference.Sample.json());

        // When
        GraphNode lastNode = deserializer.deserialize(root, flowReferenceDefinition);

        // Then
        PluginAssertion.assertThat(graph)
                .node(lastNode).is(flowReferenceNode1)
                .hasDataWithValue("ref", expectedFlowReference)
                .and().nodesCountIs(2);
    }

}
