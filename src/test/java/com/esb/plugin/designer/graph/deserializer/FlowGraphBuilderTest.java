package com.esb.plugin.designer.graph.deserializer;

import com.esb.component.Choice;
import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.TestJson;
import com.esb.plugin.designer.graph.drawable.Drawable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


class FlowGraphBuilderTest extends AbstractBuilderTest {

    @BeforeEach
    protected void setUp() {
        super.setUp();
        mockComponentDescriptor("com.esb.rest.component.RestListener");
        mockComponentDescriptor("com.esb.core.component.SetPayload1");
        mockComponentDescriptor("com.esb.core.component.SetPayload2");
        mockComponentDescriptor("com.esb.core.component.SetPayload3");
        mockComponentDescriptor("com.esb.rest.component.SetHeader");
        mockComponentDescriptor("com.esb.rest.component.SetStatus");
        mockComponentDescriptor("com.esb.logger.component.LogComponent");
        mockComponentDescriptor(Choice.class.getName());
    }

    @Test
    void shouldBuildFlowWithChoiceCorrectly() {
        // Given
        String json = readJson(TestJson.FLOW_WITH_CHOICE);
        FlowGraphBuilder builder = new FlowGraphBuilder(json, context);

        // When
        FlowGraph graph = builder.graph();

        // Then
        assertThat(graph).isNotNull();

        Drawable root = graph.root();
        assertThatComponentHasName(root, "com.esb.rest.component.RestListener");
        assertSuccessorsAre(graph, root, "com.esb.component.Choice");

        Drawable choice = firstSuccessorOf(graph, root);
        assertSuccessorsAre(graph, choice,
                "com.esb.core.component.SetPayload1",
                "com.esb.core.component.SetPayload2",
                "com.esb.core.component.SetPayload3");


        Drawable setPayload1 = getDrawableWithComponentName(graph.successors(choice), "com.esb.core.component.SetPayload1");
        assertSuccessorsAre(graph, setPayload1, "com.esb.rest.component.SetHeader");

        Drawable setPayload2 = getDrawableWithComponentName(graph.successors(choice), "com.esb.core.component.SetPayload2");
        assertSuccessorsAre(graph, setPayload2, "com.esb.rest.component.SetHeader");

        Drawable setPayload3 = getDrawableWithComponentName(graph.successors(choice), "com.esb.core.component.SetPayload3");
        assertSuccessorsAre(graph, setPayload3, "com.esb.rest.component.SetHeader");

        Drawable setHeaderDrawable = getDrawableWithComponentName(graph.successors(setPayload1), "com.esb.rest.component.SetHeader");
        assertSuccessorsAre(graph, setHeaderDrawable, "com.esb.rest.component.SetStatus");

        Drawable setStatusDrawable = firstSuccessorOf(graph, setHeaderDrawable);
        assertSuccessorsAre(graph, setStatusDrawable, "com.esb.logger.component.LogComponent");

        Drawable logComponentDrawable = firstSuccessorOf(graph, setStatusDrawable);
        assertThat(graph.successors(logComponentDrawable)).isEmpty();
    }

}