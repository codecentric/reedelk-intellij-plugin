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

public class GenericComponentDeserializerTypeObjectTest extends AbstractNodeDeserializerTest {

    @Test
    void shouldCorrectlyDeserializeTypeObject() {
        // Given
        ComponentDescriptor descriptor = ComponentDescriptor.create()
                .properties(asList(SamplePropertyDescriptors.Primitives.stringProperty, SamplePropertyDescriptors.TypeObjects.typeObjectProperty))
                .fullyQualifiedName(ComponentNode1.class.getName())
                .build();

        GenericComponentNode node = createGraphNodeInstance(GenericComponentNode.class, descriptor);

        mockContextInstantiateGraphNode(node);

        JSONObject genericComponentDefinition = new JSONObject(Json.GenericComponent.WithTypeObject.json());

        // When
        GenericComponentDeserializer deserializer = new GenericComponentDeserializer(graph, node, context);
        GraphNode lastNode = deserializer.deserialize(root, genericComponentDefinition);

        // Then
        PluginAssertion.assertThat(graph)
                .node(lastNode).is(node)
                .hasDataWithValue("stringProperty", "yet another string property")
                .hasTypeObject("typeObjectProperty")
                .hasDataWithValue("stringProperty", "sample string property")
                .hasDataWithValue("integerObjectProperty", Integer.valueOf("255"))
                .and().and().nodesCountIs(2);
    }

    @Test
    void shouldCorrectlyDeserializeSharedTypeObjectWithReference() {
        // Given
        ComponentDescriptor descriptor = ComponentDescriptor.create()
                .properties(asList(SamplePropertyDescriptors.Primitives.booleanProperty, SamplePropertyDescriptors.TypeObjects.typeObjectSharedProperty))
                .fullyQualifiedName(ComponentNode1.class.getName())
                .build();

        GenericComponentNode node = createGraphNodeInstance(GenericComponentNode.class, descriptor);

        mockContextInstantiateGraphNode(node);

        JSONObject genericComponentDefinition = new JSONObject(Json.GenericComponent.WithTypeObjectReference.json());

        // When
        GenericComponentDeserializer deserializer = new GenericComponentDeserializer(graph, node, context);
        GraphNode lastNode = deserializer.deserialize(root, genericComponentDefinition);

        // Then
        PluginAssertion.assertThat(graph)
                .node(lastNode).is(node)
                .hasDataWithValue("booleanProperty", true)
                .hasTypeObject("typeObjectSharedProperty")
                .hasDataWithValue("ref", "4ba1b6a0-9644-11e9-bc42-526af7764f64")
                .and().and().nodesCountIs(2);
    }

    // We expected empty objects still be created in the de-serialized data structure.
    @Test
    void shouldCorrectlyDeserializeSharedEmptyTypeObject() {
        // Given
        ComponentDescriptor descriptor = ComponentDescriptor.create()
                .properties(asList(SamplePropertyDescriptors.Primitives.booleanProperty, SamplePropertyDescriptors.TypeObjects.typeObjectSharedProperty))
                .fullyQualifiedName(ComponentNode1.class.getName())
                .build();

        GenericComponentNode node = createGraphNodeInstance(GenericComponentNode.class, descriptor);

        mockContextInstantiateGraphNode(node);

        JSONObject genericComponentDefinition = new JSONObject(Json.GenericComponent.WithTypeObjectEmpty.json());

        // When
        GenericComponentDeserializer deserializer = new GenericComponentDeserializer(graph, node, context);
        GraphNode lastNode = deserializer.deserialize(root, genericComponentDefinition);

        // Then
        PluginAssertion.assertThat(graph)
                .node(lastNode).is(node)
                .hasDataWithValue("booleanProperty", true)
                .hasTypeObject("typeObjectSharedProperty")
                .isEmpty()
                .and().and().nodesCountIs(2);
    }

    // We expect empty objects still be created in the de-serialized data structure.
    @Test
    void shouldCorrectlyDeserializeSharedTypeObjectWhenReferencePropertyNotPresent() {
        // Given
        ComponentDescriptor descriptor = ComponentDescriptor.create()
                .properties(asList(SamplePropertyDescriptors.Primitives.booleanProperty, SamplePropertyDescriptors.TypeObjects.typeObjectSharedProperty))
                .fullyQualifiedName(ComponentNode1.class.getName())
                .build();

        GenericComponentNode node = createGraphNodeInstance(GenericComponentNode.class, descriptor);

        mockContextInstantiateGraphNode(node);

        JSONObject genericComponentDefinition = new JSONObject(Json.GenericComponent.WithTypeObjectReferenceNotPresent.json());

        // When
        GenericComponentDeserializer deserializer = new GenericComponentDeserializer(graph, node, context);
        GraphNode lastNode = deserializer.deserialize(root, genericComponentDefinition);

        // Then
        PluginAssertion.assertThat(graph)
                .node(lastNode).is(node)
                .hasDataWithValue("booleanProperty", true)
                .hasTypeObject("typeObjectSharedProperty")
                .isEmpty()
                .and().and().nodesCountIs(2);
    }

    // We expected empty objects still be created in the de-serialized data structure.
    @Test
    void shouldCorrectlyDeserializeMissingSharedTypeObject() {
        // Given
        ComponentDescriptor descriptor = ComponentDescriptor.create()
                .properties(asList(SamplePropertyDescriptors.Primitives.booleanProperty, SamplePropertyDescriptors.TypeObjects.typeObjectSharedProperty))
                .fullyQualifiedName(ComponentNode1.class.getName())
                .build();

        GenericComponentNode node = createGraphNodeInstance(GenericComponentNode.class, descriptor);

        mockContextInstantiateGraphNode(node);

        JSONObject genericComponentDefinition = new JSONObject(Json.GenericComponent.WithTypeObjectMissing.json());

        // When
        GenericComponentDeserializer deserializer = new GenericComponentDeserializer(graph, node, context);
        GraphNode lastNode = deserializer.deserialize(root, genericComponentDefinition);

        // Then
        PluginAssertion.assertThat(graph)
                .node(lastNode).is(node)
                .hasDataWithValue("booleanProperty", true)
                .hasTypeObject("typeObjectSharedProperty")
                .isEmpty()
                .and().and().nodesCountIs(2);
    }
}
