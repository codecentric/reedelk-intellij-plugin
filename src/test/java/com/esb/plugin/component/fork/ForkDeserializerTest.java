package com.esb.plugin.component.fork;

import com.esb.plugin.AbstractDeserializerTest;
import com.esb.plugin.assertion.PluginAssertion;
import com.esb.plugin.fixture.Json;
import com.esb.plugin.graph.node.GraphNode;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

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
        JSONObject forkDefinition = new JSONObject(Json.Fork.Sample.json());

         // When
        GraphNode lastNode = deserializer.deserialize(root, forkDefinition);

        // Then
        PluginAssertion.assertThat(graph)

                .node(lastNode)
                .is(componentNode5)

                .and()
                .successorOf(forkNode)
                .containsExactly(componentNode3, componentNode1)

                .and()
                .successorOf(componentNode3)
                .containsExactly(componentNode2)

                .and()
                .successorOf(componentNode1)
                .containsExactly(componentNode4)

                .and()
                .successorOf(componentNode2)
                .containsExactly(stopNode1)

                .and()
                .successorOf(componentNode4)
                .containsExactly(stopNode1)

                .and()
                .successorOf(stopNode1)
                .containsExactly(componentNode5)

                .and()
                .nodesCountIs(8);
    }

}
