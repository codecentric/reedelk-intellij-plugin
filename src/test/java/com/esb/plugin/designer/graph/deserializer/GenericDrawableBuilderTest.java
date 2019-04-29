package com.esb.plugin.designer.graph.deserializer;

import com.esb.plugin.designer.editor.component.Component;
import com.esb.plugin.designer.graph.GraphNode;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GenericDrawableBuilderTest extends AbstractBuilderTest {

    private static final String GENERIC_COMPONENT_NAME = "com.esb.component.GenericComponent";

    private GenericDrawableBuilder builder;

    @BeforeEach
    protected void setUp() {
        super.setUp();
        builder = new GenericDrawableBuilder(graph, context);
    }

    @Test
    void shouldBuildGenericDrawableCorrectly() {
        // Given
        Component givenComponent = mockComponent(GENERIC_COMPONENT_NAME);

        JSONObject componentDefinition = ComponentDefinitionBuilder
                .forComponent(GENERIC_COMPONENT_NAME)
                .build();

        // When
        GraphNode genericDrawable = builder.build(root, componentDefinition);

        // Then
        assertThat(graph.nodesCount()).isEqualTo(2);

        Component actualComponent = genericDrawable.component();
        assertThat(actualComponent).isEqualTo(givenComponent);
    }

}
