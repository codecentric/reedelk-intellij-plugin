package com.esb.plugin.component.type.fork;

import com.esb.plugin.AbstractNodeDeserializerTest;
import com.esb.plugin.assertion.PluginAssertion;
import com.esb.plugin.graph.node.GraphNode;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static com.esb.plugin.fixture.Json.Fork;

@MockitoSettings(strictness = Strictness.LENIENT)
class ForkDeserializerTest extends AbstractNodeDeserializerTest {

    private ForkDeserializer deserializer;

    @BeforeEach
    protected void setUp() {
        super.setUp();
        deserializer = new ForkDeserializer(graph, context);
    }

    @Test
    void shouldDeserializeForkDefinitionCorrectly() {
         // Given
        JSONObject forkDefinition = new JSONObject(Fork.Sample.json());

         // When
        GraphNode lastNode = deserializer.deserialize(root, forkDefinition);

        // Then
        PluginAssertion.assertThat(graph)
                .node(lastNode).is(stopNode1)
                .and().successorsOf(stopNode1).isEmpty()
                .and().successorsOf(forkNode1).areExactly(componentNode3, componentNode1)
                .and().successorsOf(componentNode3).isOnly(componentNode2)
                .and().successorsOf(componentNode1).isOnly(componentNode4)
                .and().successorsOf(componentNode2).isOnly(stopNode1)
                .and().successorsOf(componentNode4).isOnly(stopNode1)
                .and().nodesCountIs(7)
                .node(forkNode1).hasDataWithValue("threadPoolSize", 3);
    }

}
