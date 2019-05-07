package com.esb.plugin.component.flowreference;

import com.esb.component.FlowReference;
import com.esb.plugin.component.ComponentData;
import com.esb.plugin.graph.deserializer.AbstractBuilderTest;
import com.esb.plugin.graph.deserializer.ComponentDefinitionBuilder;
import com.esb.plugin.graph.node.GraphNode;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class FlowReferenceDeserializerTest extends AbstractBuilderTest {

    private FlowReferenceDeserializer deserializer;

    @BeforeEach
    protected void setUp() {
        super.setUp();
        deserializer = new FlowReferenceDeserializer(graph, context);
    }

    @Test
    void shouldBuildFlowReferenceCorrectly() {
        // Given
        ComponentData givenComponentData = mockComponent(FlowReference.class.getName(), FlowReferenceNode.class);

        JSONObject componentDefinition = ComponentDefinitionBuilder.forComponent(FlowReference.class.getName())
                .with("ref", UUID.randomUUID().toString())
                .build();

        // When
        GraphNode flowReference = deserializer.deserialize(root, componentDefinition);

        // Then
        assertThat(graph.nodesCount()).isEqualTo(2);

        ComponentData actualComponentData = flowReference.component();
        assertThat(actualComponentData).isEqualTo(givenComponentData);
    }
}
