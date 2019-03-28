package com.esb.plugin.designer.graph.builder;

import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.TestJson;
import com.esb.plugin.designer.graph.drawable.Drawable;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


class FlowGraphBuilderTest extends AbstractBuilderTest {

    @Test
    void shouldBuildFlowWithChoiceCorrectly() {
        // Given
        String json = readJson(TestJson.FLOW_WITH_CHOICE);
        FlowGraphBuilder builder = new FlowGraphBuilder(json);

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
        Drawable stopDrawable = firstSuccessorOf(graph, setPayload1);
        assertSuccessorsAre(graph, stopDrawable, "com.esb.rest.component.SetHeader");

        Drawable setHeaderDrawable = firstSuccessorOf(graph, stopDrawable);
        assertSuccessorsAre(graph, setHeaderDrawable, "com.esb.rest.component.SetStatus");

        Drawable setStatusDrawable = firstSuccessorOf(graph, setHeaderDrawable);
        assertSuccessorsAre(graph, setStatusDrawable, "com.esb.logger.component.LogComponent");

        Drawable logComponentDrawable = firstSuccessorOf(graph, setStatusDrawable);
        assertThat(graph.successors(logComponentDrawable)).isEmpty();
    }

    @Test
    void shouldBuildFlowWithForkJoinCorrectly() {
        // Given

        // When

        // Then
    }

    @Test
    void shouldBuildFlowWithGenericComponentCorrectly() {
        // Given

        // When

        // Then
    }

    @Test
    void shouldBuildFlowWithFlowReferenceCorrectly() {
        // Given

        // When

        // Then
    }

    @Test
    void shouldBuildEmptyFlowCorrectly() {
        // Given

        // When

        // Then
    }

    @Test
    void shouldThrowExceptionWhenJsonIsNotParsable() {
        // Given

        // When

        // Then
    }

}