package com.esb.plugin.component.generic;

import com.esb.plugin.AbstractDeserializerTest;
import com.esb.plugin.assertion.PluginAssertion;
import com.esb.plugin.component.ComponentDescriptor;
import com.esb.plugin.fixture.ComponentNode1;
import com.esb.plugin.graph.node.GraphNode;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static com.esb.plugin.fixture.Json.GenericComponent;
import static java.util.Arrays.asList;

@MockitoSettings(strictness = Strictness.LENIENT)
class GenericComponentDeserializerTest extends AbstractDeserializerTest {

    private GenericComponentDeserializer deserializer;

    @BeforeEach
    protected void setUp() {
        super.setUp();
        deserializer = new GenericComponentDeserializer(graph, context);
    }

    @Test
    void shouldDeserializeGenericComponentCorrectly() {
        // Given
        ComponentDescriptor descriptor = ComponentDescriptor.create()
                .displayName("Test Component")
                .fullyQualifiedName(ComponentNode1.class.getName())
                .propertiesNames(asList("property1", "property2", "property3"))
                .build();

        GenericComponentNode node =
                mockComponentGraphNode(ComponentNode1.class, GenericComponentNode.class, descriptor);


        JSONObject genericComponentDefinition = new JSONObject(GenericComponent.Sample.json());

        // When
        GraphNode lastNode = deserializer.deserialize(root, genericComponentDefinition);

        // Then
        PluginAssertion.assertThat(graph)
                .node(lastNode).is(node)
                .hasDataWithValue("property1", "first property")
                .hasDataWithValue("property2", "second property")
                .hasDataWithValue("property3", "third property")
                .and().nodesCountIs(2);
    }

}
