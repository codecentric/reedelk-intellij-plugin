package com.reedelk.plugin.component.type.router;

import com.reedelk.plugin.assertion.PluginAssertion;
import com.reedelk.plugin.component.ComponentData;
import com.reedelk.plugin.graph.deserializer.AbstractNodeDeserializerTest;
import com.reedelk.plugin.graph.node.GraphNode;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.reedelk.plugin.fixture.Json.Router;
import static com.reedelk.runtime.commons.JsonParser.Implementor;
import static org.assertj.core.api.Assertions.assertThat;

class RouterDeserializerTest extends AbstractNodeDeserializerTest {

    private RouterDeserializer deserializer;

    @BeforeEach
    protected void setUp() {
        super.setUp();
        deserializer = new RouterDeserializer(graph, routerNode1, context);
    }

    @Test
    void shouldDeserializeRouterDefinitionCorrectly() {
        // Given
        JSONObject routerDefinition = new JSONObject(Router.Sample.json());

        // When
        GraphNode lastNode = deserializer.deserialize(root, routerDefinition);

        // Then
        PluginAssertion.assertThat(graph)
                .node(lastNode).is(stopNode1)
                .and().successorsOf(routerNode1).areExactly(componentNode3, componentNode2, componentNode5)
                .and().successorsOf(componentNode3).isOnly(componentNode1)
                .and().successorsOf(componentNode2).isOnly(componentNode4)
                .and().successorsOf(componentNode5).isOnly(componentNode6)
                .and().predecessorOf(lastNode).containsExactly(componentNode1, componentNode4, componentNode6)
                .and().nodesCountIs(9) // total nodes include: root, stop node and all the nodes belonging to this router
                .node(routerNode1).hasDataWithValue(Implementor.description(), "A simple description");


        assertExistsConditionMatching("1 == 1", routerNode1);
        assertExistsConditionMatching("1 != 0", routerNode1);
        assertExistsConditionMatching("otherwise", routerNode1);
    }

    private void assertExistsConditionMatching(String expectedCondition, RouterNode targetNode) {
        ComponentData componentData = targetNode.componentData();
        List<RouterConditionRoutePair> when = componentData.get(RouterNode.DATA_CONDITION_ROUTE_PAIRS);
        boolean matchesCondition = when.stream().anyMatch(routerConditionRoutePair ->
                routerConditionRoutePair.getCondition().equals(expectedCondition));
        assertThat(matchesCondition).isTrue();
    }
}
