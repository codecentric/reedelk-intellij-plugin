package com.esb.plugin.component.choice;

import com.esb.component.Choice;
import com.esb.component.Stop;
import com.esb.plugin.component.generic.GenericComponentGraphNode;
import com.esb.plugin.component.stop.StopGraphNode;
import com.esb.plugin.graph.deserializer.AbstractBuilderTest;
import com.esb.plugin.graph.node.GraphNode;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.esb.plugin.graph.deserializer.ComponentDefinitionBuilder.createNextComponentsArray;
import static com.esb.plugin.graph.deserializer.ComponentDefinitionBuilder.forComponent;
import static org.assertj.core.api.Assertions.assertThat;

class ChoiceGraphNodeBuilderTest extends AbstractBuilderTest {

    private final String COMPONENT_1_NAME = "com.esb.component.Name1";
    private final String COMPONENT_2_NAME = "com.esb.component.Name2";
    private final String COMPONENT_3_NAME = "com.esb.component.Name3";
    private final String COMPONENT_4_NAME = "com.esb.component.Name4";
    private final String COMPONENT_5_NAME = "com.esb.component.Name5";
    private final String COMPONENT_6_NAME = "com.esb.component.Name6";

    private ChoiceGraphNodeBuilder builder;

    @BeforeEach
    protected void setUp() {
        super.setUp();
        builder = new ChoiceGraphNodeBuilder(graph, context);

        mockComponent(COMPONENT_1_NAME, GenericComponentGraphNode.class);
        mockComponent(COMPONENT_2_NAME, GenericComponentGraphNode.class);
        mockComponent(COMPONENT_3_NAME, GenericComponentGraphNode.class);
        mockComponent(COMPONENT_4_NAME, GenericComponentGraphNode.class);
        mockComponent(COMPONENT_5_NAME, GenericComponentGraphNode.class);
        mockComponent(COMPONENT_6_NAME, GenericComponentGraphNode.class);
        mockComponent(Choice.class.getName(), ChoiceGraphNode.class);
        mockComponent(Stop.class.getName(), StopGraphNode.class);
    }

    @Test
    void shouldBuildChoiceCorrectly() {
        // Given
        JSONArray whenArray = new JSONArray();
        whenArray.put(conditionalBranch("1 == 1", COMPONENT_3_NAME, COMPONENT_1_NAME));
        whenArray.put(conditionalBranch("'hello' == 'hello1'", COMPONENT_2_NAME, COMPONENT_4_NAME));

        JSONArray otherwiseComponents = createNextComponentsArray(COMPONENT_5_NAME, COMPONENT_6_NAME);

        JSONObject componentDefinition = forComponent(Choice.class.getName())
                .with("when", whenArray)
                .with("otherwise", otherwiseComponents)
                .build();

        // When
        GraphNode stopDrawable = builder.build(root, componentDefinition);

        // Then: last node must be a stop node
        assertThat(stopDrawable).isInstanceOf(StopGraphNode.class);

        // Then: check successors of choice
        GraphNode choice = firstSuccessorOf(graph, root);
        assertSuccessorsAre(graph, choice, COMPONENT_3_NAME, COMPONENT_2_NAME, COMPONENT_5_NAME);

        GraphNode component3Drawable = getNodeHavingComponentName(graph.successors(choice), COMPONENT_3_NAME);
        assertSuccessorsAre(graph, component3Drawable, COMPONENT_1_NAME);

        GraphNode component2Drawable = getNodeHavingComponentName(graph.successors(choice), COMPONENT_2_NAME);
        assertSuccessorsAre(graph, component2Drawable, COMPONENT_4_NAME);

        GraphNode component5Drawable = getNodeHavingComponentName(graph.successors(choice), COMPONENT_5_NAME);
        assertSuccessorsAre(graph, component5Drawable, COMPONENT_6_NAME);

        // Then: check predecessors of last stop node
        assertPredecessorsAre(graph, stopDrawable, COMPONENT_1_NAME, COMPONENT_4_NAME, COMPONENT_6_NAME);

        // Then: check that the number of nodes in the graph is correct
        int expectedNodes = 9;
        assertThat(graph.nodesCount()).isEqualTo(expectedNodes);
    }

    private static JSONObject conditionalBranch(String condition, String... componentsNames) {
        JSONObject object = new JSONObject();
        object.put("condition", condition);
        object.put("next", createNextComponentsArray(componentsNames));
        return object;
    }

}
