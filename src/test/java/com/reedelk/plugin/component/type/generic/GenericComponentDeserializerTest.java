package com.reedelk.plugin.component.type.generic;

import com.reedelk.plugin.assertion.PluginAssertion;
import com.reedelk.plugin.component.domain.ComponentDefaultDescriptor;
import com.reedelk.plugin.component.domain.ComponentDescriptor;
import com.reedelk.plugin.component.domain.ComponentPropertyDescriptor;
import com.reedelk.plugin.component.domain.TypePrimitiveDescriptor;
import com.reedelk.plugin.fixture.ComponentNode1;
import com.reedelk.plugin.graph.deserializer.AbstractNodeDeserializerTest;
import com.reedelk.plugin.graph.node.GraphNode;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static com.reedelk.plugin.fixture.Json.GenericComponent;
import static java.util.Arrays.asList;

@MockitoSettings(strictness = Strictness.LENIENT)
class GenericComponentDeserializerTest extends AbstractNodeDeserializerTest {

    private GenericComponentDeserializer deserializer;

    @BeforeEach
    protected void setUp() {
        super.setUp();
        deserializer = new GenericComponentDeserializer(graph, context);
    }

    @Test
    void shouldDeserializeGenericComponentCorrectly() {
        // Given
        ComponentDescriptor descriptor = ComponentDefaultDescriptor.create()
                .displayName("Test Component")
                .fullyQualifiedName(ComponentNode1.class.getName())
                .propertyDescriptors(asList(
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

    private ComponentPropertyDescriptor createPropertyDefinition(String propertyName, Class<?> propertyClass) {
        TypePrimitiveDescriptor typePrimitive = new TypePrimitiveDescriptor(propertyClass);
        return ComponentPropertyDescriptor.builder()
                .displayName("A property name")
                .propertyName(propertyName)
                .type(typePrimitive)
                .build();
    }
}
