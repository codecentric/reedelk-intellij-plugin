package com.reedelk.plugin.component.type.unknown;

import com.reedelk.plugin.assertion.PluginAssertion;
import com.reedelk.plugin.fixture.Json;
import com.reedelk.plugin.graph.deserializer.AbstractNodeDeserializerTest;
import com.reedelk.plugin.graph.node.GraphNode;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Objects;

class UnknownDeserializerTest extends AbstractNodeDeserializerTest {

    private UnknownDeserializer deserializer;

    @BeforeEach
    protected void setUp() {
        super.setUp();
        deserializer = new UnknownDeserializer(graph, componentNode1, context);
    }

    @Test
    void shouldDeserializeUnknownComponentCorrectly() {
        // Given
        JSONObject unknownComponentDefinition = new JSONObject(Json.Unknown.Sample.json());

        // When
        GraphNode lastNode = deserializer.deserialize(root, unknownComponentDefinition);

        // Then
        PluginAssertion.assertThat(graph)
                .node(lastNode)
                .hasDataWithValue("property1", "property 1 value")
                .hasDataWithValue("property2", actual -> {
                    if (!(actual instanceof JSONObject)) return false;
                    JSONObject nestedObject = (JSONObject) actual;
                    return Objects.equals(nestedObject.get("property3"), 3) &&
                            Objects.equals(nestedObject.get("property4"), "property 4 value");
                })
                .and().nodesCountIs(2);
    }
}
