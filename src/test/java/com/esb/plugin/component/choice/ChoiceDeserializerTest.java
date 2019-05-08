package com.esb.plugin.component.choice;

import com.esb.api.component.Component;
import com.esb.component.Choice;
import com.esb.plugin.AbstractDeserializerTest;
import com.esb.plugin.assertion.PluginAssertion;
import com.esb.plugin.component.stop.StopNode;
import com.esb.plugin.fixture.*;
import com.esb.plugin.graph.node.GraphNode;
import com.google.common.collect.Iterables;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.esb.plugin.graph.deserializer.ComponentDefinitionBuilder.createNextComponentsArray;
import static com.esb.plugin.graph.deserializer.ComponentDefinitionBuilder.forComponent;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

class ChoiceDeserializerTest extends AbstractDeserializerTest {

    private ChoiceDeserializer deserializer;

    private StopNode stopNode;
    private ChoiceNode choiceNode;

    private GraphNode componentNode1;
    private GraphNode componentNode2;
    private GraphNode componentNode3;
    private GraphNode componentNode4;
    private GraphNode componentNode5;
    private GraphNode componentNode6;

    @BeforeEach
    protected void setUp() {
        super.setUp();
        deserializer = new ChoiceDeserializer(graph, context);

        stopNode = mockStopNode();
        choiceNode = mockChoiceNode();
        componentNode1 = mockGenericComponentNode(ComponentNode1.class);
        componentNode2 = mockGenericComponentNode(ComponentNode2.class);
        componentNode3 = mockGenericComponentNode(ComponentNode3.class);
        componentNode4 = mockGenericComponentNode(ComponentNode4.class);
        componentNode5 = mockGenericComponentNode(ComponentNode5.class);
        componentNode6 = mockGenericComponentNode(ComponentNode6.class);
    }

    @Test
    void shouldBuildChoiceCorrectly() {
        // Given
        JSONArray when = new JSONArray();
        when.put(conditionalBranch("1 == 1", ComponentNode3.class, ComponentNode1.class));
        when.put(conditionalBranch("1 != 0", ComponentNode2.class, ComponentNode4.class));

        JSONArray otherwise = createNextComponentsArray(ComponentNode5.class.getName(), ComponentNode6.class.getName());

        JSONObject choiceDefinition = forComponent(Choice.class.getName())
                .with("when", when)
                .with("otherwise", otherwise)
                .build();

        // When
        GraphNode lastNode = deserializer.deserialize(root, choiceDefinition);

        // Then: last node must be a stop node
        assertThat(lastNode).isEqualTo(stopNode);

        // Then: check successors of choice
        PluginAssertion.assertThat(graph)

                .node(lastNode)
                .isEqualTo(stopNode)

                .and()
                .successorOf(choiceNode)
                .containsExactly(componentNode3, componentNode2, componentNode5)

                .and()
                .successorOf(componentNode3)
                .containsExactly(componentNode1)

                .and()
                .successorOf(componentNode2)
                .containsExactly(componentNode4)

                .and()
                .successorOf(componentNode5)
                .containsExactly(componentNode6)

                .and()
                .predecessorOf(lastNode)
                .containsExactly(componentNode1, componentNode4, componentNode6)

                .and()
                .nodesCountIs(9);


    }

    @SafeVarargs
    private static JSONObject conditionalBranch(String condition, Class<? extends Component>... componentNodesClasses) {
        String[] fullyQualifiedNames = fullyQualifiedNamesOf(componentNodesClasses);
        JSONObject object = new JSONObject();
        object.put("condition", condition);
        object.put("next", createNextComponentsArray(fullyQualifiedNames));
        return object;
    }

    @SafeVarargs
    private static String[] fullyQualifiedNamesOf(Class<? extends Component>... componentNodesClasses) {
        List<String> componentNodesList = stream(componentNodesClasses)
                .map(Class::getName)
                .collect(toList());
        return Iterables.toArray(componentNodesList, String.class);
    }

}
