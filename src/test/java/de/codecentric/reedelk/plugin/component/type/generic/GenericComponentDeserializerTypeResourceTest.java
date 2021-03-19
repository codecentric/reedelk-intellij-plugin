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

public class GenericComponentDeserializerTypeResourceTest extends AbstractNodeDeserializerTest {

    @Test
    void shouldCorrectlyDeserializeGenericComponentWithResourceProperty() {
        // Given
        ComponentDescriptor descriptor = ComponentDescriptor.create()
                .properties(asList(SamplePropertyDescriptors.Primitives.booleanProperty, SamplePropertyDescriptors.SpecialTypes.resourceProperty))
                .fullyQualifiedName(ComponentNode1.class.getName())
                .build();

        GenericComponentNode node = createGraphNodeInstance(GenericComponentNode.class, descriptor);

        mockContextInstantiateGraphNode(node);

        JSONObject genericComponentDefinition = new JSONObject(Json.GenericComponent.WithResourceProperty.json());

        // When
        GenericComponentDeserializer deserializer = new GenericComponentDeserializer(graph, node, context);
        GraphNode lastNode = deserializer.deserialize(root, genericComponentDefinition);

        // Then
        PluginAssertion.assertThat(graph)
                .node(lastNode).is(node)
                .hasDataWithValue("booleanProperty", true)
                .hasDataWithValue("resourceProperty", "metadata/schema/person.schema.json")
                .and().nodesCountIs(2);
    }
}
