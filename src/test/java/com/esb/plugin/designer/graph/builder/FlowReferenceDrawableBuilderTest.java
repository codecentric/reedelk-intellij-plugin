package com.esb.plugin.designer.graph.builder;

import com.esb.plugin.designer.editor.component.ComponentDescriptor;
import com.esb.plugin.designer.graph.FlowGraphImpl;
import com.esb.plugin.designer.graph.drawable.Drawable;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static com.esb.plugin.commons.SystemComponents.FLOW_REFERENCE;
import static org.assertj.core.api.Assertions.assertThat;

class FlowReferenceDrawableBuilderTest extends AbstractBuilderTest {

    @Mock
    private Drawable root;

    private FlowGraphImpl graph;
    private FlowReferenceDrawableBuilder builder;

    @BeforeEach
    void setUp() {
        this.graph = new FlowGraphImpl();
        this.graph.add(null, this.root);
        this.builder = new FlowReferenceDrawableBuilder();
    }

    @Test
    void shouldBuildFlowReferenceCorrectly() {
        // Given
        JSONObject componentDefinition = ComponentDefinitionBuilder.forComponent(FLOW_REFERENCE.qualifiedName())
                .with("ref", "aabbccdd11223344")
                .build();

        // When
        Drawable flowReference = builder.build(root, componentDefinition, graph);

        // Then
        assertThat(graph.nodesCount()).isEqualTo(2);

        ComponentDescriptor component = flowReference.component();
        assertThat(component.getName()).isEqualTo(FLOW_REFERENCE.qualifiedName());
    }
}
