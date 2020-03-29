package com.reedelk.plugin.component.type.generic;

import com.google.common.collect.ImmutableMap;
import com.reedelk.module.descriptor.model.ComponentDescriptor;
import com.reedelk.plugin.assertion.PluginAssertion;
import com.reedelk.plugin.fixture.ComponentNode1;
import com.reedelk.plugin.graph.deserializer.AbstractNodeDeserializerTest;
import com.reedelk.plugin.graph.node.GraphNode;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.reedelk.module.descriptor.model.TypeObjectDescriptor.TypeObject;
import static com.reedelk.plugin.component.type.generic.SamplePropertyDescriptors.Primitives.stringProperty;
import static com.reedelk.plugin.component.type.generic.SamplePropertyDescriptors.SpecialTypes.listProperty;
import static com.reedelk.plugin.component.type.generic.SamplePropertyDescriptors.SpecialTypes.listPropertyWithCustomValueType;
import static com.reedelk.plugin.fixture.Json.GenericComponent;
import static java.util.Arrays.asList;

public class GenericComponentDeserializerTypeListTest extends AbstractNodeDeserializerTest {

    @Test
    void shouldCorrectlyDeserializeGenericComponentWithListProperty() {
        // Given
        ComponentDescriptor descriptor = ComponentDescriptor.create()
                .propertyDescriptors(asList(stringProperty, listProperty))
                .fullyQualifiedName(ComponentNode1.class.getName())
                .build();

        GenericComponentNode node = createGraphNodeInstance(GenericComponentNode.class, descriptor);

        mockContextInstantiateGraphNode(node);

        JSONObject definition = new JSONObject(GenericComponent.WithNotEmptyListProperty.json());

        // When
        GenericComponentDeserializer deserializer = new GenericComponentDeserializer(graph, node, context);
        GraphNode lastNode = deserializer.deserialize(root, definition);

        // Then
        PluginAssertion.assertThat(graph)
                .node(lastNode).is(node)
                .hasDataWithValue("stringProperty", "first property")
                .hasDataWithValue("listProperty", asList("one", "two", "three"))
                .and().nodesCountIs(2);
    }

    @Test
    void shouldCorrectlyDeserializeGenericComponentWithEmptyListProperty() {
        // Given
        ComponentDescriptor descriptor = ComponentDescriptor.create()
                .propertyDescriptors(asList(stringProperty, listProperty))
                .fullyQualifiedName(ComponentNode1.class.getName())
                .build();

        GenericComponentNode node = createGraphNodeInstance(GenericComponentNode.class, descriptor);

        mockContextInstantiateGraphNode(node);

        JSONObject definition = new JSONObject(GenericComponent.WithEmptyListProperty.json());

        // When
        GenericComponentDeserializer deserializer = new GenericComponentDeserializer(graph, node, context);
        GraphNode lastNode = deserializer.deserialize(root, definition);

        // Then
        PluginAssertion.assertThat(graph)
                .node(lastNode).is(node)
                .hasDataWithValue("stringProperty", "first property")
                .hasDataWithValue("listProperty", Collections.emptyList())
                .and().nodesCountIs(2);
    }

    @SuppressWarnings("unchecked")
    @Test
    void shouldCorrectlyDeserializeComponentWithListCustomValueType() {
        // Given
        ComponentDescriptor descriptor = ComponentDescriptor.create()
                .propertyDescriptors(asList(stringProperty, listPropertyWithCustomValueType))
                .fullyQualifiedName(ComponentNode1.class.getName())
                .build();

        GenericComponentNode node = createGraphNodeInstance(GenericComponentNode.class, descriptor);

        mockContextInstantiateGraphNode(node);

        JSONObject definition =
                new JSONObject(GenericComponent.WithNotEmptyListPropertyCustomValueType.json());

        // When
        GenericComponentDeserializer deserializer = new GenericComponentDeserializer(graph, node, context);
        GraphNode lastNode = deserializer.deserialize(root, definition);

        // Then
        PluginAssertion.assertThat(graph)
                .node(lastNode).is(node)
                .hasDataWithValue("stringProperty", "first property")
                .hasDataWithValue("listPropertyWithCustomValueType", actual -> {
                    List<TypeObject> deSerializedList = (List<TypeObject>) actual;

                    boolean containsItem200 = assertListContains(deSerializedList, ImmutableMap.of(
                            "integerObjectProperty", 200,
                            "stringProperty", "200 string property",
                            "implementor", "com.reedelk.plugin.fixture.ComponentNode2"));

                    boolean containsItem400 = assertListContains(deSerializedList, ImmutableMap.of(
                            "integerObjectProperty", 400,
                            "stringProperty", "400 string property",
                            "implementor", "com.reedelk.plugin.fixture.ComponentNode2"));

                    boolean correctSize = deSerializedList.size() == 2;

                    return containsItem200 && containsItem400 && correctSize;
                })
                .and().nodesCountIs(2);
    }

    @SuppressWarnings("unchecked")
    @Test
    void shouldCorrectlyDeserializeComponentWithEmptyListCustomValueType() {
        // Given
        ComponentDescriptor descriptor = ComponentDescriptor.create()
                .propertyDescriptors(asList(stringProperty, listPropertyWithCustomValueType))
                .fullyQualifiedName(ComponentNode1.class.getName())
                .build();

        GenericComponentNode node = createGraphNodeInstance(GenericComponentNode.class, descriptor);

        mockContextInstantiateGraphNode(node);

        JSONObject definition =
                new JSONObject(GenericComponent.WithEmptyMapPropertyCustomValueType.json());

        // When
        GenericComponentDeserializer deserializer = new GenericComponentDeserializer(graph, node, context);
        GraphNode lastNode = deserializer.deserialize(root, definition);

        // Then
        PluginAssertion.assertThat(graph)
                .node(lastNode).is(node)
                .hasDataWithValue("stringProperty", "first property")
                .hasDataWithValue("listPropertyWithCustomValueType", actual -> {
                    List<TypeObject> deSerializedList = (List<TypeObject>) actual;
                    return deSerializedList.isEmpty();
                })
                .and().nodesCountIs(2);
    }

    private boolean assertListContains(List<TypeObject> items, Map<String, Object> properties) {
        return items.stream().anyMatch(typeObject ->
                properties.keySet().stream().anyMatch(key -> {
                    Object actualValue = typeObject.get(key);
                    Object expectedValue = properties.get(key);
                    return Objects.equals(actualValue, expectedValue);
                }));
    }
}
