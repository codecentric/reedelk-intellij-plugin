package com.esb.plugin.graph.deserializer;

import com.esb.component.Choice;
import com.esb.component.Stop;
import com.esb.plugin.component.choice.ChoiceNode;
import com.esb.plugin.component.generic.GenericComponentNode;
import com.esb.plugin.component.stop.StopNode;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.TestJson;
import com.esb.plugin.graph.node.GraphNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


class GraphDeserializerTest extends AbstractBuilderTest {

    @BeforeEach
    protected void setUp() {
        super.setUp();
        mockComponent("com.esb.rest.component.RestListener", GenericComponentNode.class);
        mockComponent("com.esb.core.component.SetPayload1", GenericComponentNode.class);
        mockComponent("com.esb.core.component.SetPayload2", GenericComponentNode.class);
        mockComponent("com.esb.core.component.SetPayload3", GenericComponentNode.class);
        mockComponent("com.esb.rest.component.SetHeader", GenericComponentNode.class);
        mockComponent("com.esb.rest.component.SetStatus", GenericComponentNode.class);
        mockComponent("com.esb.logger.component.LogComponent", GenericComponentNode.class);
        mockComponent(Choice.class.getName(), ChoiceNode.class);
        mockComponent(Stop.class.getName(), StopNode.class);
    }

    @Test
    void shouldBuildFlowWithChoiceCorrectly() {
        // Given
        String json = readJson(TestJson.FLOW_WITH_CHOICE);
        GraphDeserializer builder = new GraphDeserializer(json, context);

        // When
        FlowGraph graph = builder.deserialize();

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