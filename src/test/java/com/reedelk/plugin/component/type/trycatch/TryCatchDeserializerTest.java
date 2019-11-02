package com.reedelk.plugin.component.type.trycatch;

import com.reedelk.plugin.assertion.PluginAssertion;
import com.reedelk.plugin.graph.deserializer.AbstractNodeDeserializerTest;
import com.reedelk.plugin.graph.node.GraphNode;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.reedelk.plugin.fixture.Json.TryCatch;

class TryCatchDeserializerTest extends AbstractNodeDeserializerTest {

    private TryCatchDeserializer deserializer;

    @BeforeEach
    protected void setUp() {
        super.setUp();
        deserializer = new TryCatchDeserializer(graph, context);
    }

    @Test
    void shouldDeserializeTryCatchDefinitionCorrectly() {
        // Given
        JSONObject tryCatchDefinition = new JSONObject(TryCatch.Sample.json());

        // When
        GraphNode lastNode = deserializer.deserialize(root, tryCatchDefinition);

        // Then
        PluginAssertion.assertThat(graph)
                .node(lastNode).is(stopNode1)
                .and().successorsOf(root).isOnly(tryCatchNode1)
                .and().successorsOf(tryCatchNode1).areExactly(componentNode3, componentNode5)
                .and().successorsOf(componentNode3).isOnly(componentNode1)
                .and().successorsOf(componentNode5).isOnly(componentNode6)
                .and().nodesCountIs(7);
    }
}