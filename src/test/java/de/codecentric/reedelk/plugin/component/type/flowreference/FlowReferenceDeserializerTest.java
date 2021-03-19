package de.codecentric.reedelk.plugin.component.type.flowreference;

import de.codecentric.reedelk.plugin.assertion.PluginAssertion;
import de.codecentric.reedelk.plugin.fixture.Json;
import de.codecentric.reedelk.plugin.graph.deserializer.AbstractNodeDeserializerTest;
import de.codecentric.reedelk.plugin.graph.node.GraphNode;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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

        JSONObject flowReferenceDefinition = new JSONObject(Json.FlowReference.Sample.json());

        // When
        GraphNode lastNode = deserializer.deserialize(root, flowReferenceDefinition);

        // Then
        PluginAssertion.assertThat(graph)
                .node(lastNode).is(flowReferenceNode1)
                .hasDataWithValue("ref", expectedFlowReference)
                .and().nodesCountIs(2);
    }

}
