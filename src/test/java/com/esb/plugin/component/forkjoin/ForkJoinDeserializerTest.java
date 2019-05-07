package com.esb.plugin.component.forkjoin;

import com.esb.component.Fork;
import com.esb.component.Stop;
import com.esb.plugin.component.generic.GenericComponentNode;
import com.esb.plugin.component.stop.StopNode;
import com.esb.plugin.graph.deserializer.AbstractBuilderTest;
import com.esb.plugin.graph.node.GraphNode;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.esb.plugin.graph.deserializer.ComponentDefinitionBuilder.createNextComponentsArray;
import static com.esb.plugin.graph.deserializer.ComponentDefinitionBuilder.forComponent;
import static org.assertj.core.api.Assertions.assertThat;

class ForkJoinDeserializerTest extends AbstractBuilderTest {

    private final String JOIN_COMPONENT_NAME = "com.esb.component.JoinString";

    private final String COMPONENT_1_NAME = "com.esb.component.Name1";
    private final String COMPONENT_2_NAME = "com.esb.component.Name2";
    private final String COMPONENT_3_NAME = "com.esb.component.Name3";
    private final String COMPONENT_4_NAME = "com.esb.component.Name4";

    private ForkJoinDeserializer deserializer;

    @BeforeEach
    protected void setUp() {
        super.setUp();
        deserializer = new ForkJoinDeserializer(graph, context);

        mockComponent(COMPONENT_1_NAME, GenericComponentNode.class);
        mockComponent(COMPONENT_2_NAME, GenericComponentNode.class);
        mockComponent(COMPONENT_3_NAME, GenericComponentNode.class);
        mockComponent(COMPONENT_4_NAME, GenericComponentNode.class);
        mockComponent(JOIN_COMPONENT_NAME, GenericComponentNode.class);
        mockComponent(Fork.class.getName(), ForkJoinNode.class);
        mockComponent(Stop.class.getName(), StopNode.class);
    }

    @Test
    void shouldBuildForkJoinCorrectly() {
        // Given
        JSONArray forkArray = new JSONArray();
        forkArray.put(createNextObject(COMPONENT_3_NAME, COMPONENT_2_NAME));
        forkArray.put(createNextObject(COMPONENT_1_NAME, COMPONENT_4_NAME));

        JSONObject componentDefinition = forComponent(Fork.class.getName())
                .with("threadPoolSize", 3)
                .with("fork", forkArray)
                .with("join", forComponent(JOIN_COMPONENT_NAME)
                        .with("prop1", "Test")
                        .with("prop2", 3L)
                        .build())
                .build();

        // When
        GraphNode joinDrawable = deserializer.deserialize(root, componentDefinition);

        // Then: last node must be a join node
        assertThat(joinDrawable.component().getFullyQualifiedName()).isEqualTo(JOIN_COMPONENT_NAME);

        // Then: check successors of fork
        GraphNode fork = firstSuccessorOf(graph, root);
        assertSuccessorsAre(graph, fork, COMPONENT_3_NAME, COMPONENT_1_NAME);

        GraphNode component3Drawable = getNodeHavingComponentName(graph.successors(fork), COMPONENT_3_NAME);
        assertSuccessorsAre(graph, component3Drawable, COMPONENT_2_NAME);

        GraphNode component2Drawable = getNodeHavingComponentName(graph.successors(component3Drawable), COMPONENT_2_NAME);
        assertSuccessorsAre(graph, component2Drawable, Stop.class.getName());

        GraphNode component1Drawable = getNodeHavingComponentName(graph.successors(fork), COMPONENT_1_NAME);
        assertSuccessorsAre(graph, component1Drawable, COMPONENT_4_NAME);

        GraphNode component4Drawable = getNodeHavingComponentName(graph.successors(component1Drawable), COMPONENT_4_NAME);
        assertSuccessorsAre(graph, component4Drawable, Stop.class.getName());

        GraphNode stopDrawable = getNodeHavingComponentName(graph.successors(component4Drawable), Stop.class.getName());
        GraphNode stopSuccessor = graph.successors(stopDrawable).get(0);
        assertThat(joinDrawable).isEqualTo(stopSuccessor);

        // Then: check that the number of nodes in the graph is correct
        int expectedNodes = 8;
        assertThat(graph.nodesCount()).isEqualTo(expectedNodes);
    }

    private JSONObject createNextObject(String... componentNames) {
        JSONObject nextObject = new JSONObject();
        nextObject.put("next", createNextComponentsArray(componentNames));
        return nextObject;
    }

}
