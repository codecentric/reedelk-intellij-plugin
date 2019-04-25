package com.esb.plugin.designer.graph.builder;

import com.esb.component.Choice;
import com.esb.plugin.designer.graph.FlowGraphImpl;
import com.esb.plugin.designer.graph.drawable.Drawable;
import com.esb.plugin.designer.graph.drawable.StopDrawable;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static com.esb.plugin.designer.graph.builder.ComponentDefinitionBuilder.createNextComponentsArray;
import static com.esb.plugin.designer.graph.builder.ComponentDefinitionBuilder.forComponent;
import static org.assertj.core.api.Assertions.assertThat;

class ChoiceDrawableBuilderTest extends AbstractBuilderTest {

    private final String COMPONENT_1_NAME = "com.esb.component.Name1";
    private final String COMPONENT_2_NAME = "com.esb.component.Name2";
    private final String COMPONENT_3_NAME = "com.esb.component.Name3";
    private final String COMPONENT_4_NAME = "com.esb.component.Name4";

    @Mock
    private Drawable root;
    @Mock
    private BuilderContext context;

    private FlowGraphImpl graph;
    private ChoiceDrawableBuilder builder;

    @BeforeEach
    void setUp() {
        this.graph = new FlowGraphImpl();
        this.graph.root(root);
        this.builder = new ChoiceDrawableBuilder(graph, context);
    }

    @Test
    void shouldBuildChoiceCorrectly() {
        // Given
        JSONArray whenArray = new JSONArray();
        whenArray.put(conditionalBranch("1 == 1", COMPONENT_3_NAME, COMPONENT_1_NAME));
        whenArray.put(conditionalBranch("'hello' == 'hello1'", COMPONENT_2_NAME, COMPONENT_4_NAME));

        JSONArray otherwiseComponents = createNextComponentsArray(COMPONENT_4_NAME, COMPONENT_3_NAME);

        JSONObject componentDefinition = forComponent(Choice.class.getName())
                .with("when", whenArray)
                .with("otherwise", otherwiseComponents)
                .build();

        // When
        Drawable stopDrawable = builder.build(root, componentDefinition);

        // Then: last node must be a stop drawable
        assertThat(stopDrawable).isInstanceOf(StopDrawable.class);

        // Then: check successors of choice
        Drawable choice = firstSuccessorOf(graph, root);
        assertSuccessorsAre(graph, choice, COMPONENT_3_NAME, COMPONENT_2_NAME, COMPONENT_4_NAME);

        Drawable component3Drawable = getDrawableWithComponentName(graph.successors(choice), COMPONENT_3_NAME);
        assertSuccessorsAre(graph, component3Drawable, COMPONENT_1_NAME);

        Drawable component2Drawable = getDrawableWithComponentName(graph.successors(choice), COMPONENT_2_NAME);
        assertSuccessorsAre(graph, component2Drawable, COMPONENT_4_NAME);

        Drawable component4Drawable = getDrawableWithComponentName(graph.successors(choice), COMPONENT_4_NAME);
        assertSuccessorsAre(graph, component4Drawable, COMPONENT_3_NAME);

        // Then: check predecessors of last stop node
        assertPredecessorsAre(graph, stopDrawable, COMPONENT_1_NAME, COMPONENT_4_NAME, COMPONENT_3_NAME);

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
