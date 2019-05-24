package com.esb.plugin.component.type.generic;

import com.esb.plugin.AbstractDeserializerTest;
import com.esb.plugin.assertion.PluginAssertion;
import com.esb.plugin.component.domain.*;
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
        ComponentDescriptor descriptor = DefaultComponentDescriptor.create()
                .displayName("Test Component")
                .fullyQualifiedName(ComponentNode1.class.getName())
                .propertyDefinitions(asList(
                        createPropertyDefinition("property1", String.class),
                        createPropertyDefinition("property2", String.class),
                        createPropertyDefinition("property3", String.class)))
                .build();

        GenericComponentNode node = createGraphNodeInstance(GenericComponentNode.class, descriptor);
        mockContextInstantiateGraphNode(node);


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

    // Fixme
    private ComponentPropertyDescriptor createPropertyDefinition(String propertyName, Class<?> propertyClass) {
        return new ComponentPropertyDescriptor(
                propertyName,
                new PrimitiveTypeDescriptor(propertyClass),
                "A property name", "", PropertyRequired.REQUIRED);
    }

}
