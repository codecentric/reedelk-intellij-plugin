package com.esb.plugin.designer.graph.builder;

import com.esb.plugin.designer.editor.component.ComponentDescriptor;
import com.esb.plugin.designer.graph.FlowGraphImpl;
import com.esb.plugin.designer.graph.drawable.Drawable;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;

class GenericDrawableBuilderTest extends AbstractBuilderTest {

    private static final String GENERIC_COMPONENT_NAME = "com.esb.component.GenericComponent";

    @Mock
    private Drawable root;

    private FlowGraphImpl graph;
    private GenericDrawableBuilder builder;

    @BeforeEach
    void setUp() {
        this.graph = new FlowGraphImpl();
        this.graph.add(null, this.root);
        this.builder = new GenericDrawableBuilder();
    }

    @Test
    void shouldBuildGenericDrawableCorrectly() {
        // Given
        JSONObject componentDefinition = ComponentDefinitionBuilder.forComponent(GENERIC_COMPONENT_NAME)
                .build();

        // When
        Drawable genericDrawable = builder.build(root, componentDefinition, graph);

        // Then
        assertThat(graph.nodesCount()).isEqualTo(2);

        ComponentDescriptor component = genericDrawable.component();
        assertThat(component).isNotNull();
        assertThat(component.getName()).isEqualTo(GENERIC_COMPONENT_NAME);
    }

}
