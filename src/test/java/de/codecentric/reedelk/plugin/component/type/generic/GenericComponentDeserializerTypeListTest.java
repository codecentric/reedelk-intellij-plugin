package de.codecentric.reedelk.plugin.component.type.generic;

import de.codecentric.reedelk.plugin.assertion.PluginAssertion;
import com.google.common.collect.ImmutableMap;
import de.codecentric.reedelk.module.descriptor.model.component.ComponentDescriptor;
import de.codecentric.reedelk.module.descriptor.model.property.ObjectDescriptor;
import de.codecentric.reedelk.plugin.fixture.ComponentNode1;
import de.codecentric.reedelk.plugin.fixture.Json;
import de.codecentric.reedelk.plugin.graph.deserializer.AbstractNodeDeserializerTest;
import de.codecentric.reedelk.plugin.graph.node.GraphNode;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.Arrays.asList;

public class GenericComponentDeserializerTypeListTest extends AbstractNodeDeserializerTest {

    @Test
    void shouldCorrectlyDeserializeGenericComponentWithListProperty() {
        // Given
        ComponentDescriptor descriptor = ComponentDescriptor.create()
                .properties(asList(SamplePropertyDescriptors.Primitives.stringProperty, SamplePropertyDescriptors.SpecialTypes.listProperty))
                .fullyQualifiedName(ComponentNode1.class.getName())
                .build();

        GenericComponentNode node = createGraphNodeInstance(GenericComponentNode.class, descriptor);

        mockContextInstantiateGraphNode(node);

        JSONObject definition = new JSONObject(Json.GenericComponent.WithNotEmptyListProperty.json());

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
                .properties(asList(SamplePropertyDescriptors.Primitives.stringProperty, SamplePropertyDescriptors.SpecialTypes.listProperty))
                .fullyQualifiedName(ComponentNode1.class.getName())
                .build();

        GenericComponentNode node = createGraphNodeInstance(GenericComponentNode.class, descriptor);

        mockContextInstantiateGraphNode(node);

        JSONObject definition = new JSONObject(Json.GenericComponent.WithEmptyListProperty.json());

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
                .properties(asList(SamplePropertyDescriptors.Primitives.stringProperty, SamplePropertyDescriptors.SpecialTypes.listPropertyWithCustomValueType))
                .fullyQualifiedName(ComponentNode1.class.getName())
                .build();

        GenericComponentNode node = createGraphNodeInstance(GenericComponentNode.class, descriptor);

        mockContextInstantiateGraphNode(node);

        JSONObject definition =
                new JSONObject(Json.GenericComponent.WithNotEmptyListPropertyCustomValueType.json());

        // When
        GenericComponentDeserializer deserializer = new GenericComponentDeserializer(graph, node, context);
        GraphNode lastNode = deserializer.deserialize(root, definition);

        // Then
        PluginAssertion.assertThat(graph)
                .node(lastNode).is(node)
                .hasDataWithValue("stringProperty", "first property")
                .hasDataWithValue("listPropertyWithCustomValueType", actual -> {
                    List<ObjectDescriptor.TypeObject> deSerializedList = (List<ObjectDescriptor.TypeObject>) actual;

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
                .properties(asList(SamplePropertyDescriptors.Primitives.stringProperty, SamplePropertyDescriptors.SpecialTypes.listPropertyWithCustomValueType))
                .fullyQualifiedName(ComponentNode1.class.getName())
                .build();

        GenericComponentNode node = createGraphNodeInstance(GenericComponentNode.class, descriptor);

        mockContextInstantiateGraphNode(node);

        JSONObject definition =
                new JSONObject(Json.GenericComponent.WithEmptyMapPropertyCustomValueType.json());

        // When
        GenericComponentDeserializer deserializer = new GenericComponentDeserializer(graph, node, context);
        GraphNode lastNode = deserializer.deserialize(root, definition);

        // Then
        PluginAssertion.assertThat(graph)
                .node(lastNode).is(node)
                .hasDataWithValue("stringProperty", "first property")
                .hasDataWithValue("listPropertyWithCustomValueType", actual -> {
                    List<ObjectDescriptor.TypeObject> deSerializedList = (List<ObjectDescriptor.TypeObject>) actual;
                    return deSerializedList.isEmpty();
                })
                .and().nodesCountIs(2);
    }

    private boolean assertListContains(List<ObjectDescriptor.TypeObject> items, Map<String, Object> properties) {
        return items.stream().anyMatch(typeObject ->
                properties.keySet().stream().anyMatch(key -> {
                    Object actualValue = typeObject.get(key);
                    Object expectedValue = properties.get(key);
                    return Objects.equals(actualValue, expectedValue);
                }));
    }
}
