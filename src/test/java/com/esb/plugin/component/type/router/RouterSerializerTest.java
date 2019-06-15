package com.esb.plugin.component.type.router;

import com.esb.internal.commons.JsonParser;
import com.esb.plugin.AbstractGraphTest;
import com.esb.plugin.component.domain.ComponentData;
import com.esb.plugin.graph.FlowGraph;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.util.List;

import static com.esb.plugin.component.type.router.RouterNode.DATA_CONDITION_ROUTE_PAIRS;
import static com.esb.plugin.fixture.Json.Router;
import static java.util.Arrays.asList;

public class RouterSerializerTest extends AbstractGraphTest {

    private RouterSerializer serializer;

    @BeforeEach
    protected void setUp() {
        super.setUp();
        serializer = new RouterSerializer();
    }

    @Test
    void shouldCorrectlySerializeRouterNode() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, routerNode1);

        graph.add(routerNode1, componentNode3);
        graph.add(routerNode1, componentNode2);
        graph.add(routerNode1, componentNode5);

        graph.add(componentNode3, componentNode1);
        graph.add(componentNode2, componentNode4);
        graph.add(componentNode5, componentNode6);

        graph.add(componentNode1, componentNode7);
        graph.add(componentNode4, componentNode7);
        graph.add(componentNode6, componentNode7);

        routerNode1.addToScope(componentNode1);
        routerNode1.addToScope(componentNode2);
        routerNode1.addToScope(componentNode3);
        routerNode1.addToScope(componentNode4);
        routerNode1.addToScope(componentNode5);
        routerNode1.addToScope(componentNode6);

        List<RouterConditionRoutePair> routerConditionPairs = asList(
                new RouterConditionRoutePair("1 == 1", componentNode3),
                new RouterConditionRoutePair("1 != 0", componentNode2),
                new RouterConditionRoutePair(com.esb.system.component.Router.DEFAULT_CONDITION, componentNode5));

        ComponentData component = routerNode1.componentData();
        component.set(DATA_CONDITION_ROUTE_PAIRS, routerConditionPairs);
        component.set(JsonParser.Implementor.description(), "A simple description");

        JSONArray sequence = new JSONArray();

        // When
        serializer.serialize(graph, sequence, routerNode1, componentNode7);

        // Then
        JSONObject serializedObject = sequence.getJSONObject(0);

        String actualJson = serializedObject.toString(2);
        String expectedJson = Router.Sample.json();
        JSONAssert.assertEquals(expectedJson, actualJson, true);
    }

}
