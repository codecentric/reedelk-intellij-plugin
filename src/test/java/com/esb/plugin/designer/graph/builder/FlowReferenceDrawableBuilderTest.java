package com.esb.plugin.designer.graph.builder;

import com.esb.component.FlowReference;
import com.esb.plugin.designer.editor.component.ComponentDescriptor;
import com.esb.plugin.designer.graph.FlowGraphImpl;
import com.esb.plugin.designer.graph.drawable.Drawable;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

class FlowReferenceDrawableBuilderTest extends AbstractBuilderTest {

    @Mock
    private Drawable root;
    @Mock
    private BuilderContext context;

    private FlowGraphImpl graph;
    private FlowReferenceDrawableBuilder builder;

    @BeforeEach
    void setUp() {
        graph = new FlowGraphImpl();
        graph.root(root);
        builder = new FlowReferenceDrawableBuilder(graph, context);
    }

    @Test
    void shouldBuildFlowReferenceCorrectly() {
        // Given
        ComponentDescriptor flowReferenceComponent = ComponentDescriptor.create()
                .fullyQualifiedName(FlowReference.class.getName())
                .build();

        doReturn(flowReferenceComponent)
                .when(context)
                .instantiateComponent(FlowReference.class.getName());

        JSONObject componentDefinition = ComponentDefinitionBuilder.forComponent(FlowReference.class.getName())
                .with("ref", "aabbccdd11223344")
                .build();

        // When
        Drawable flowReference = builder.build(root, componentDefinition);

        // Then
        assertThat(graph.nodesCount()).isEqualTo(2);

        ComponentDescriptor component = flowReference.component();
        assertThat(component).isEqualTo(flowReferenceComponent);
    }
}
