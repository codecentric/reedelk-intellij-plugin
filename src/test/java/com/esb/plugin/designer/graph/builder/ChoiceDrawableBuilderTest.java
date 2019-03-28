package com.esb.plugin.designer.graph.builder;

import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.drawable.Drawable;
import com.esb.plugin.designer.graph.drawable.StopDrawable;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static com.esb.plugin.commons.SystemComponents.CHOICE;
import static com.esb.plugin.designer.graph.builder.ComponentDefinitionBuilder.createNextComponentsArray;
import static com.esb.plugin.designer.graph.builder.ComponentDefinitionBuilder.forComponent;
import static org.assertj.core.api.Assertions.assertThat;

class ChoiceDrawableBuilderTest extends AbstractBuilderTest {

    private static final String COMPONENT_1_NAME = "com.esb.component.Name1";
    private static final String COMPONENT_2_NAME = "com.esb.component.Name2";
    private static final String COMPONENT_3_NAME = "com.esb.component.Name3";
    private static final String COMPONENT_4_NAME = "com.esb.component.Name4";

    @Mock
    private Drawable root;

    private FlowGraph graph;
    private ChoiceDrawableBuilder builder;

    private static JSONObject conditionalBranch(String condition, String... componentsNames) {
        JSONObject object = new JSONObject();
        object.put("condition", condition);
        object.put("next", createNextComponentsArray(componentsNames));
        return object;
    }

    @BeforeEach
    void setUp() {
        this.graph = new FlowGraph();
        this.graph.add(null, this.root);
        this.builder = new ChoiceDrawableBuilder();
    }

    @Test
    void shouldBuildChoiceCorrectly() {
        // Given
        JSONArray whenArray = new JSONArray();
        whenArray.put(conditionalBranch("1 == 1", COMPONENT_3_NAME, COMPONENT_1_NAME));
        whenArray.put(conditionalBranch("'hello' == 'hello1'", COMPONENT_2_NAME, COMPONENT_4_NAME));

        JSONArray otherwiseComponents = createNextComponentsArray(COMPONENT_4_NAME, COMPONENT_3_NAME);

        JSONObject componentDefinition = forComponent(CHOICE.qualifiedName())
                .with("when", whenArray)
                .with("otherwise", otherwiseComponents)
                .build();

        // When
        Drawable stopDrawable = builder.build(root, componentDefinition, graph);

        // Then: last node must be a stop drawable
        assertThat(stopDrawable).isInstanceOf(StopDrawable.class);

        Drawable choice = firstSuccessorOf(graph, root);
        assertSuccessorsAre(graph, choice, COMPONENT_3_NAME, COMPONENT_2_NAME, COMPONENT_4_NAME);

        Drawable component3Drawable = getDrawableWithComponentName(graph.successors(choice), COMPONENT_3_NAME);
        assertSuccessorsAre(graph, component3Drawable, COMPONENT_1_NAME);

        Drawable component2Drawable = getDrawableWithComponentName(graph.successors(choice), COMPONENT_2_NAME);
        assertSuccessorsAre(graph, component2Drawable, COMPONENT_4_NAME);

        Drawable component4Drawable = getDrawableWithComponentName(graph.successors(choice), COMPONENT_4_NAME);
        assertSuccessorsAre(graph, component4Drawable, COMPONENT_3_NAME);

        assertPredecessorsAre(graph, stopDrawable, COMPONENT_1_NAME, COMPONENT_4_NAME, COMPONENT_3_NAME);
    }

}
