package com.esb.plugin.component.flowreference;

import com.esb.component.FlowReference;
import com.esb.plugin.AbstractDeserializerTest;
import com.esb.plugin.assertion.PluginAssertion;
import com.esb.plugin.component.ComponentData;
import com.esb.plugin.graph.deserializer.ComponentDefinitionBuilder;
import com.esb.plugin.graph.node.GraphNode;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class FlowReferenceDeserializerTest extends AbstractDeserializerTest {

    private FlowReferenceDeserializer deserializer;

    @BeforeEach
    protected void setUp() {
        super.setUp();
        deserializer = new FlowReferenceDeserializer(graph, context);
    }

    @Test
    void shouldBuildFlowReferenceCorrectly() {
        // Given
        String givenRef = UUID.randomUUID().toString();

        FlowReferenceNode flowReferenceNode = mockFlowReferenceNode();

        JSONObject componentDefinition = ComponentDefinitionBuilder.forComponent(FlowReference.class.getName())
                .with("ref", givenRef)
                .build();

        // When
        GraphNode lastNode = deserializer.deserialize(root, componentDefinition);

        // Then
        assertThat(lastNode).isEqualTo(flowReferenceNode);

        PluginAssertion.assertThat(graph)
                .successorOf(root)
                .containsExactly(flowReferenceNode)
                .and()
                .nodesCountIs(2);


        ComponentData actualComponentData = flowReferenceNode.component();
        assertThat(actualComponentData.get("ref")).isEqualTo(givenRef);
    }

}
