package com.reedelk.plugin.component.type.trycatch;

import com.reedelk.plugin.AbstractGraphTest;
import com.reedelk.plugin.graph.FlowGraph;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import static com.reedelk.plugin.fixture.Json.TryCatch;

class TryCatchSerializerTest extends AbstractGraphTest {

    private TryCatchSerializer serializer;

    @BeforeEach
    protected void setUp() {
        super.setUp();
        serializer = new TryCatchSerializer();
    }

    @Test
    void shouldCorrectlySerializeTryCatchNode() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, tryCatchNode1);

        graph.add(tryCatchNode1, componentNode3, 0);
        graph.add(tryCatchNode1, componentNode5, 1);
        graph.add(componentNode3, componentNode1);
        graph.add(componentNode5, componentNode6);
        graph.add(componentNode6, componentNode7);
        graph.add(componentNode1, componentNode7);

        tryCatchNode1.addToScope(componentNode3);
        tryCatchNode1.addToScope(componentNode1);
        tryCatchNode1.addToScope(componentNode5);
        tryCatchNode1.addToScope(componentNode6);

        JSONArray sequence = new JSONArray();

        // When
        serializer.serialize(graph, sequence, tryCatchNode1, componentNode7);

        // Then
        JSONObject serializedObject = sequence.getJSONObject(0);

        String actualJson = serializedObject.toString(2);
        String expectedJson = TryCatch.Sample.json();
        JSONAssert.assertEquals(expectedJson, actualJson, true);
    }
}