package com.esb.plugin.component.fork;

import com.esb.plugin.AbstractDeserializerTest;
import com.esb.plugin.assertion.PluginAssertion;
import com.esb.plugin.graph.node.GraphNode;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static com.esb.plugin.fixture.Json.Fork;

@MockitoSettings(strictness = Strictness.LENIENT)
class ForkDeserializerTest extends AbstractDeserializerTest {

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
                .node(lastNode).is(componentNode5)
                .and().successorsOf(forkNode1).areExactly(componentNode3, componentNode1)
                .and().successorsOf(componentNode3).isOnly(componentNode2)
                .and().successorsOf(componentNode1).isOnly(componentNode4)
                .and().successorsOf(componentNode2).isOnly(stopNode1)
                .and().successorsOf(componentNode4).isOnly(stopNode1)
                .and().successorsOf(stopNode1).isOnly(componentNode5)
                .and().nodesCountIs(8)
                .node(forkNode1).hasDataWithValue("threadPoolSize", 3);
    }

}
