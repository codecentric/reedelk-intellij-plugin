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

import java.util.Map;
import java.util.Objects;

import static java.util.Arrays.asList;

public class GenericComponentDeserializerTypeMapTest extends AbstractNodeDeserializerTest {

    @Test
    void shouldCorrectlyDeserializeGenericComponentWithMapProperty() {
        // Given
        ComponentDescriptor descriptor = ComponentDescriptor.create()
                .properties(asList(SamplePropertyDescriptors.Primitives.stringProperty, SamplePropertyDescriptors.SpecialTypes.mapProperty))
                .fullyQualifiedName(ComponentNode1.class.getName())
                .build();

        GenericComponentNode node = createGraphNodeInstance(GenericComponentNode.class, descriptor);

        mockContextInstantiateGraphNode(node);

        JSONObject genericComponentDefinition = new JSONObject(Json.GenericComponent.WithNotEmptyMapProperty.json());

        // When
        GenericComponentDeserializer deserializer = new GenericComponentDeserializer(graph, node, context);
        GraphNode lastNode = deserializer.deserialize(root, genericComponentDefinition);

        // Then
        PluginAssertion.assertThat(graph)
                .node(lastNode).is(node)
                .hasDataWithValue("stringProperty", "first property")
                .hasDataWithValue("mapProperty", ImmutableMap.of("key1", "value1", "key2", "value2"))
                .and().nodesCountIs(2);
    }

    @Test
    void shouldCorrectlyDeserializeGenericComponentWithEmptyMapProperty() {
        // Given
        ComponentDescriptor descriptor = ComponentDescriptor.create()
                .properties(asList(SamplePropertyDescriptors.Primitives.stringProperty, SamplePropertyDescriptors.SpecialTypes.mapProperty))
                .fullyQualifiedName(ComponentNode1.class.getName())
                .build();

        GenericComponentNode node = createGraphNodeInstance(GenericComponentNode.class, descriptor);

        mockContextInstantiateGraphNode(node);

        JSONObject genericComponentDefinition = new JSONObject(Json.GenericComponent.WithEmptyMapProperty.json());

        // When
        GenericComponentDeserializer deserializer = new GenericComponentDeserializer(graph, node, context);
        GraphNode lastNode = deserializer.deserialize(root, genericComponentDefinition);

        // Then
        PluginAssertion.assertThat(graph)
                .node(lastNode).is(node)
                .hasDataWithValue("stringProperty", "first property")
                .hasDataWithValue("mapProperty", ImmutableMap.of())
                .and().nodesCountIs(2);
    }

    @SuppressWarnings("unchecked")
    @Test
    void shouldCorrectlyDeserializeComponentWithMapCustomValueType() {
        // Given
        ComponentDescriptor descriptor = ComponentDescriptor.create()
                .properties(asList(SamplePropertyDescriptors.Primitives.stringProperty, SamplePropertyDescriptors.SpecialTypes.mapPropertyWithCustomValueType))
                .fullyQualifiedName(ComponentNode1.class.getName())
                .build();

        GenericComponentNode node = createGraphNodeInstance(GenericComponentNode.class, descriptor);

        mockContextInstantiateGraphNode(node);

        JSONObject genericComponentDefinition =
                new JSONObject(Json.GenericComponent.WithNotEmptyMapPropertyCustomValueType.json());

        // When
        GenericComponentDeserializer deserializer = new GenericComponentDeserializer(graph, node, context);
        GraphNode lastNode = deserializer.deserialize(root, genericComponentDefinition);

        // Then
        PluginAssertion.assertThat(graph)
                .node(lastNode).is(node)
                .hasDataWithValue("stringProperty", "first property")
                .hasDataWithValue("mapPropertyWithCustomValueType", actual -> {
                    Map<String, ObjectDescriptor.TypeObject> deSerializedMap = (Map<String, ObjectDescriptor.TypeObject>) actual;
                    boolean containsAllKeys = deSerializedMap.containsKey("200") && deSerializedMap.containsKey("400");
                    boolean matches200 = containsProperties(deSerializedMap.get("200"), ImmutableMap.of(
                            "integerObjectProperty", 200,
                            "stringProperty", "200 string property",
                            "implementor", "de.codecentric.reedelk.plugin.fixture.ComponentNode2"));
                    boolean matches400 = containsProperties(deSerializedMap.get("400"), ImmutableMap.of(
                            "integerObjectProperty", 400,
                            "stringProperty", "400 string property",
                            "implementor", "de.codecentric.reedelk.plugin.fixture.ComponentNode2"));
                    return containsAllKeys && matches200 && matches400;
                })
                .and().nodesCountIs(2);
    }

    @SuppressWarnings("unchecked")
    @Test
    void shouldCorrectlyDeserializeComponentWithEmptyMapCustomValueType() {
        // Given
        ComponentDescriptor descriptor = ComponentDescriptor.create()
                .properties(asList(SamplePropertyDescriptors.Primitives.stringProperty, SamplePropertyDescriptors.SpecialTypes.mapPropertyWithCustomValueType))
                .fullyQualifiedName(ComponentNode1.class.getName())
                .build();

        GenericComponentNode node = createGraphNodeInstance(GenericComponentNode.class, descriptor);

        mockContextInstantiateGraphNode(node);

        JSONObject genericComponentDefinition =
                new JSONObject(Json.GenericComponent.WithEmptyMapPropertyCustomValueType.json());

        // When
        GenericComponentDeserializer deserializer = new GenericComponentDeserializer(graph, node, context);
        GraphNode lastNode = deserializer.deserialize(root, genericComponentDefinition);

        // Then
        PluginAssertion.assertThat(graph)
                .node(lastNode).is(node)
                .hasDataWithValue("stringProperty", "first property")
                .hasDataWithValue("mapPropertyWithCustomValueType", actual -> {
                    Map<String, ObjectDescriptor.TypeObject> deSerializedMap = (Map<String, ObjectDescriptor.TypeObject>) actual;
                    return deSerializedMap.isEmpty();
                })
                .and().nodesCountIs(2);
    }

    private boolean containsProperties(ObjectDescriptor.TypeObject actual, Map<String,Object> properties) {
        return properties.keySet().stream().allMatch(key -> {
            Object actualValue = actual.get(key);
            Object expectedValue = properties.get(key);
            return Objects.equals(actualValue, expectedValue);
        });
    }
}
