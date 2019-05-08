package com.esb.plugin.component.fork;

import com.esb.component.Fork;
import com.esb.component.Stop;
import com.esb.plugin.AbstractDeserializerTest;
import com.esb.plugin.component.stop.StopNode;
import com.esb.plugin.fixture.*;
import com.esb.plugin.graph.node.GraphNode;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.esb.plugin.graph.deserializer.ComponentDefinitionBuilder.createNextComponentsArray;
import static com.esb.plugin.graph.deserializer.ComponentDefinitionBuilder.forComponent;
import static org.assertj.core.api.Assertions.assertThat;

class ForkDeserializerTest extends AbstractDeserializerTest {

    private ForkDeserializer deserializer;

    private ForkNode forkNode;
    private StopNode stopNode;

    private GraphNode componentNode1;
    private GraphNode componentNode2;
    private GraphNode componentNode3;
    private GraphNode componentNode4;
    private GraphNode componentNode5;
    private GraphNode componentNode6;

    @BeforeEach
    protected void setUp() {
        super.setUp();
        deserializer = new ForkDeserializer(graph, context);

        componentNode1 = mockGenericComponentNode(ComponentNode1.class);
        componentNode2 = mockGenericComponentNode(ComponentNode2.class);
        componentNode3 = mockGenericComponentNode(ComponentNode3.class);
        componentNode4 = mockGenericComponentNode(ComponentNode4.class);
        componentNode5 = mockGenericComponentNode(ComponentNode5.class);
        componentNode6 = mockGenericComponentNode(ComponentNode6.class);

        forkNode = mockForkNode();
        stopNode = mockStopNode();
    }

    @Test
    void shouldDeserializeForkDefinitionCorrectly() {
        // Given
        JSONArray forkArray = new JSONArray();
        forkArray.put(createNextObject(COMPONENT_3_NAME, COMPONENT_2_NAME));
        forkArray.put(createNextObject(COMPONENT_1_NAME, COMPONENT_4_NAME));

        JSONObject forkDefinition = forComponent(Fork.class.getName())
                .with("threadPoolSize", 3)
                .with("fork", forkArray)
                .with("join", forComponent(JOIN_COMPONENT_NAME)
                        .with("prop1", "Test")
                        .with("prop2", 3L)
                        .build())
                .build();

        // When
        GraphNode joinDrawable = deserializer.deserialize(root, forkDefinition);

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
