package com.esb.plugin.designer.graph.builder;

import com.esb.component.Fork;
import com.esb.plugin.designer.graph.FlowGraphImpl;
import com.esb.plugin.designer.graph.drawable.Drawable;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static com.esb.plugin.designer.graph.builder.ComponentDefinitionBuilder.createNextComponentsArray;
import static com.esb.plugin.designer.graph.builder.ComponentDefinitionBuilder.forComponent;
import static org.assertj.core.api.Assertions.assertThat;

class ForkJoinDrawableBuilderTest extends AbstractBuilderTest {

    private final String STOP_COMPONENT_NAME = "Stop";
    private final String JOIN_COMPONENT_NAME = "com.esb.component.JoinString";

    private final String COMPONENT_1_NAME = "com.esb.component.Name1";
    private final String COMPONENT_2_NAME = "com.esb.component.Name2";
    private final String COMPONENT_3_NAME = "com.esb.component.Name3";
    private final String COMPONENT_4_NAME = "com.esb.component.Name4";

    @Mock
    private Drawable root;
    @Mock
    private BuilderContext context;

    private FlowGraphImpl graph;
    private ForkJoinDrawableBuilder builder;

    @BeforeEach
    void setUp() {
        this.graph = new FlowGraphImpl();
        this.graph.root(root);
        this.builder = new ForkJoinDrawableBuilder(graph, context);
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
        Drawable joinDrawable = builder.build(root, componentDefinition);

        // Then: last node must be a join drawable
        assertThat(joinDrawable.component().getFullyQualifiedName()).isEqualTo(JOIN_COMPONENT_NAME);

        // Then: check successors of fork
        Drawable fork = firstSuccessorOf(graph, root);
        assertSuccessorsAre(graph, fork, COMPONENT_3_NAME, COMPONENT_1_NAME);

        Drawable component3Drawable = getDrawableWithComponentName(graph.successors(fork), COMPONENT_3_NAME);
        assertSuccessorsAre(graph, component3Drawable, COMPONENT_2_NAME);

        Drawable component2Drawable = getDrawableWithComponentName(graph.successors(component3Drawable), COMPONENT_2_NAME);
        assertSuccessorsAre(graph, component2Drawable, STOP_COMPONENT_NAME);

        Drawable component1Drawable = getDrawableWithComponentName(graph.successors(fork), COMPONENT_1_NAME);
        assertSuccessorsAre(graph, component1Drawable, COMPONENT_4_NAME);

        Drawable component4Drawable = getDrawableWithComponentName(graph.successors(component1Drawable), COMPONENT_4_NAME);
        assertSuccessorsAre(graph, component4Drawable, STOP_COMPONENT_NAME);

        Drawable stopDrawable = getDrawableWithComponentName(graph.successors(component4Drawable), STOP_COMPONENT_NAME);
        Drawable stopSuccessor = graph.successors(stopDrawable).get(0);
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
