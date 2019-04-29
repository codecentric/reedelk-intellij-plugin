package com.esb.plugin.designer.graph.deserializer;

import com.esb.component.FlowReference;
import com.esb.plugin.designer.editor.component.Component;
import com.esb.plugin.designer.graph.GraphNode;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class FlowReferenceDrawableBuilderTest extends AbstractBuilderTest {

    private FlowReferenceDrawableBuilder builder;

    @BeforeEach
    protected void setUp() {
        super.setUp();
        builder = new FlowReferenceDrawableBuilder(graph, context);
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
