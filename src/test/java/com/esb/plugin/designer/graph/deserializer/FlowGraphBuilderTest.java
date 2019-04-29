package com.esb.plugin.designer.graph.deserializer;

import com.esb.component.Choice;
import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.GraphNode;
import com.esb.plugin.designer.graph.TestJson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


class FlowGraphBuilderTest extends AbstractBuilderTest {

    @BeforeEach
    protected void setUp() {
        super.setUp();
        mockComponent("com.esb.rest.component.RestListener");
        mockComponent("com.esb.core.component.SetPayload1");
        mockComponent("com.esb.core.component.SetPayload2");
        mockComponent("com.esb.core.component.SetPayload3");
        mockComponent("com.esb.rest.component.SetHeader");
        mockComponent("com.esb.rest.component.SetStatus");
        mockComponent("com.esb.logger.component.LogComponent");
        mockComponent(Choice.class.getName());
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

        GraphNode root = graph.root();
        assertThatComponentHasName(root, "com.esb.rest.component.RestListener");
        assertSuccessorsAre(graph, root, "com.esb.component.Choice");

        GraphNode choice = firstSuccessorOf(graph, root);
        assertSuccessorsAre(graph, choice,
                "com.esb.core.component.SetPayload1",
                "com.esb.core.component.SetPayload2",
                "com.esb.core.component.SetPayload3");


        GraphNode setPayload1 = getNodeHavingComponentName(graph.successors(choice), "com.esb.core.component.SetPayload1");
        assertSuccessorsAre(graph, setPayload1, "com.esb.rest.component.SetHeader");

        GraphNode setPayload2 = getNodeHavingComponentName(graph.successors(choice), "com.esb.core.component.SetPayload2");
        assertSuccessorsAre(graph, setPayload2, "com.esb.rest.component.SetHeader");

        GraphNode setPayload3 = getNodeHavingComponentName(graph.successors(choice), "com.esb.core.component.SetPayload3");
        assertSuccessorsAre(graph, setPayload3, "com.esb.rest.component.SetHeader");

        GraphNode setHeaderDrawable = getNodeHavingComponentName(graph.successors(setPayload1), "com.esb.rest.component.SetHeader");
        assertSuccessorsAre(graph, setHeaderDrawable, "com.esb.rest.component.SetStatus");

        GraphNode setStatusDrawable = firstSuccessorOf(graph, setHeaderDrawable);
        assertSuccessorsAre(graph, setStatusDrawable, "com.esb.logger.component.LogComponent");

        GraphNode logComponentDrawable = firstSuccessorOf(graph, setStatusDrawable);
        assertThat(graph.successors(logComponentDrawable)).isEmpty();
    }

}