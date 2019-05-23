package com.esb.plugin.component.type.flowreference;

import com.esb.plugin.AbstractDeserializerTest;
import com.esb.plugin.assertion.PluginAssertion;
import com.esb.plugin.graph.node.GraphNode;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static com.esb.plugin.fixture.Json.FlowReference;

@MockitoSettings(strictness = Strictness.LENIENT)
class FlowReferenceDeserializerTest extends AbstractDeserializerTest {

    private FlowReferenceDeserializer deserializer;

    @BeforeEach
    protected void setUp() {
        super.setUp();
        deserializer = new FlowReferenceDeserializer(graph, context);
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
