package com.esb.plugin.graph.deserializer;

import com.esb.plugin.AbstractDeserializerTest;
import com.esb.plugin.assertion.PluginAssertion;
import com.esb.plugin.component.choice.ChoiceNode;
import com.esb.plugin.component.flowreference.FlowReferenceNode;
import com.esb.plugin.component.fork.ForkNode;
import com.esb.plugin.component.stop.StopNode;
import com.esb.plugin.fixture.*;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.esb.plugin.SampleJson.FLOW_WITH_ALL_COMPONENTS;
import static org.assertj.core.api.Assertions.assertThat;

class GraphDeserializerTest extends AbstractDeserializerTest {

    private GraphNode componentNode1;
    private GraphNode componentNode2;
    private GraphNode componentNode3;
    private GraphNode componentNode4;
    private GraphNode componentNode5;
    private GraphNode componentNode6;

    private StopNode stopNode;
    private ForkNode forkNode;
    private ChoiceNode choiceNode;
    private FlowReferenceNode flowReferenceNode;

    @BeforeEach
    protected void setUp() {
        super.setUp();

        componentNode1 = mockGenericComponentNode(ComponentNode1.class);
        componentNode2 = mockGenericComponentNode(ComponentNode2.class);
        componentNode3 = mockGenericComponentNode(ComponentNode3.class);
        componentNode4 = mockGenericComponentNode(ComponentNode4.class);
        componentNode5 = mockGenericComponentNode(ComponentNode5.class);
        componentNode6 = mockGenericComponentNode(ComponentNode6.class);

        stopNode = mockStopNode();
        forkNode = mockForkNode();
        choiceNode = mockChoiceNode();
        flowReferenceNode = mockFlowReferenceNode();
    }

    @Test
    void shouldBuildFlowWithChoiceCorrectly() {
        // Given
        String json = FLOW_WITH_ALL_COMPONENTS.asJson();
        GraphDeserializer deserializer = new GraphDeserializer(json, context);

        // When
        FlowGraph graph = deserializer.deserialize();

        // Then
        assertThat(graph).isNotNull();

        GraphNode rootNode = graph.root();

        PluginAssertion.assertThat(graph)

                .node(rootNode)
                .isEqualTo(componentNode1)

                .and()
                .successorOf(componentNode1)
                .containsExactly(choiceNode)

                .and()
                .successorOf(choiceNode)
                .containsExactly(componentNode2, flowReferenceNode, forkNode)

                .and()
                .successorOf(componentNode2)
                .containsExactly(componentNode6)

                .and()
                .successorOf(flowReferenceNode)
                .containsExactly(componentNode6)

                .and()
                .successorOf(forkNode)
                .containsExactly(componentNode3, componentNode4)

                .and()
                .successorOf(componentNode3)
                .containsExactly(componentNode5)

                .and()
                .successorOf(componentNode4)
                .containsExactly(componentNode5)

                .and()
                .successorOf(componentNode5)
                .containsExactly(componentNode6);
    }

}