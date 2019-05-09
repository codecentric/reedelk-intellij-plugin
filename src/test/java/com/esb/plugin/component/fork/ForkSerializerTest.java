package com.esb.plugin.component.fork;

import com.esb.plugin.AbstractGraphTest;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.FlowGraphImpl;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import static com.esb.plugin.fixture.Json.Fork;

public class ForkSerializerTest extends AbstractGraphTest {

    private ForkSerializer serializer;

    @BeforeEach
    protected void setUp() {
        super.setUp();
        serializer = new ForkSerializer();
    }

    @Test
    void shouldCorrectlySerializeFork() {
        // Given
        FlowGraph graph = new FlowGraphImpl();
        graph.root(root);
        graph.add(root, forkNode1);

        graph.add(forkNode1, componentNode3);
        graph.add(forkNode1, componentNode1);

        graph.add(componentNode3, componentNode2);
        graph.add(componentNode1, componentNode4);

        graph.add(componentNode2, componentNode5);
        graph.add(componentNode4, componentNode5);

        forkNode1.componentData().set("threadPoolSize", 3);

        // When
        JSONObject serializedObject = serializer.serialize(graph, forkNode1, componentNode5);

        // Then
        String actualJson = serializedObject.toString(2);
        String expectedJson = Fork.Sample.json();
        JSONAssert.assertEquals(expectedJson, actualJson, true);
    }
}
