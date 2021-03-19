package de.codecentric.reedelk.plugin.component.type.generic;

import de.codecentric.reedelk.plugin.assertion.PluginAssertion;
import de.codecentric.reedelk.module.descriptor.model.component.ComponentDescriptor;
import de.codecentric.reedelk.plugin.fixture.ComponentNode1;
import de.codecentric.reedelk.plugin.fixture.Json;
import de.codecentric.reedelk.plugin.graph.deserializer.AbstractNodeDeserializerTest;
import de.codecentric.reedelk.plugin.graph.node.GraphNode;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import static java.util.Arrays.asList;

public class GenericComponentDeserializerTypeEnumTest extends AbstractNodeDeserializerTest {

    @Test
    void shouldCorrectlyDeserializeGenericComponentWithEnumProperty() {
        // Given
        ComponentDescriptor descriptor = ComponentDescriptor.create()
                .properties(asList(SamplePropertyDescriptors.Primitives.floatProperty, SamplePropertyDescriptors.SpecialTypes.enumProperty))
                .fullyQualifiedName(ComponentNode1.class.getName())
                .build();

        GenericComponentNode node = createGraphNodeInstance(GenericComponentNode.class, descriptor);

        mockContextInstantiateGraphNode(node);

        JSONObject genericComponentDefinition = new JSONObject(Json.GenericComponent.WithEnumProperty.json());

        // When
        GenericComponentDeserializer deserializer = new GenericComponentDeserializer(graph, node, context);
        GraphNode lastNode = deserializer.deserialize(root, genericComponentDefinition);

        // Then
        PluginAssertion.assertThat(graph)
                .node(lastNode).is(node)
                .hasDataWithValue("floatProperty", 2483.002f)
                .hasDataWithValue("enumProperty", "CERT")
                .and().nodesCountIs(2);

    }
}
