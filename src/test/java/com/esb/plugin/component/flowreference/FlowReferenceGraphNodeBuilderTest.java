package com.esb.plugin.component.flowreference;

import com.esb.component.FlowReference;
import com.esb.plugin.component.Component;
import com.esb.plugin.graph.deserializer.AbstractBuilderTest;
import com.esb.plugin.graph.deserializer.ComponentDefinitionBuilder;
import com.esb.plugin.graph.node.GraphNode;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class FlowReferenceGraphNodeBuilderTest extends AbstractBuilderTest {

    private FlowReferenceGraphNodeBuilder builder;

    @BeforeEach
    protected void setUp() {
        super.setUp();
        builder = new FlowReferenceGraphNodeBuilder(graph, context);
    }

    @Test
    void shouldBuildFlowReferenceCorrectly() {
        // Given
        Component givenComponent = mockComponent(FlowReference.class.getName());

        JSONObject componentDefinition = ComponentDefinitionBuilder.forComponent(FlowReference.class.getName())
                .with("ref", UUID.randomUUID().toString())
                .build();

        // When
        GraphNode flowReference = builder.build(root, componentDefinition);

        // Then
        assertThat(graph.nodesCount()).isEqualTo(2);

        Component actualComponent = flowReference.component();
        assertThat(actualComponent).isEqualTo(givenComponent);
    }
}
