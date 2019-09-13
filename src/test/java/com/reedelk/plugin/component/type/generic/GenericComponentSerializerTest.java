package com.reedelk.plugin.component.type.generic;

import com.reedelk.plugin.AbstractGraphTest;
import com.reedelk.plugin.component.domain.ComponentData;
import com.reedelk.plugin.component.domain.ComponentDefaultDescriptor;
import com.reedelk.plugin.component.domain.TypeObjectDescriptor;
import com.reedelk.plugin.fixture.ComponentNode1;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.node.GraphNode;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.util.HashMap;
import java.util.Map;

import static com.reedelk.plugin.component.type.generic.SamplePropertyDescriptors.*;
import static com.reedelk.plugin.fixture.Json.GenericComponent.*;
import static com.reedelk.plugin.graph.serializer.AbstractSerializer.UntilNoSuccessors;
import static java.util.Arrays.asList;

public class GenericComponentSerializerTest extends AbstractGraphTest {

    private static final UntilNoSuccessors UNTIL_NO_SUCCESSORS = new UntilNoSuccessors();

    private GenericComponentSerializer serializer;

    @BeforeEach
    protected void setUp() {
        super.setUp();
        serializer = new GenericComponentSerializer();
    }

    @Test
    void shouldCorrectlySerializeGenericComponent() {
        // Given
        ComponentData componentData = new ComponentData(ComponentDefaultDescriptor.create()
                .propertyDescriptors(asList(property1, property2, property3))
                .fullyQualifiedName(ComponentNode1.class.getName())
                .build());

        GraphNode componentNode = new GenericComponentNode(componentData);
        componentData.set("property1", "first property");
        componentData.set("property2", "second property");
        componentData.set("property3", "third property");

        // When
        String actualJson = serialize(componentNode);

        // Then
        String expectedJson = Sample.json();
        JSONAssert.assertEquals(expectedJson, actualJson, true);
    }

    @Test
    void shouldCorrectlySerializeGenericComponentWithTypeObject() {
        // Given
        ComponentData componentData = new ComponentData(ComponentDefaultDescriptor.create()
                .propertyDescriptors(asList(property1, property4))
                .fullyQualifiedName(ComponentNode1.class.getName())
                .build());

        TypeObjectDescriptor.TypeObject property4Object = componentNode2TypeDescriptor.newInstance();
        property4Object.set("property5", "property five");
        property4Object.set("property6", 255);

        GraphNode componentNode = new GenericComponentNode(componentData);
        componentData.set("property1", "first property");
        componentData.set("property4", property4Object);

        // When
        String actualJson = serialize(componentNode);

        // Then
        String expectedJson = WithTypeObject.json();
        JSONAssert.assertEquals(expectedJson, actualJson, true);
    }

    @Test
    void shouldCorrectlySerializeGenericComponentWithTypeObjectReference() {
        // Given
        ComponentData componentData = new ComponentData(ComponentDefaultDescriptor.create()
                .propertyDescriptors(asList(property1, property7))
                .fullyQualifiedName(ComponentNode1.class.getName())
                .build());

        TypeObjectDescriptor.TypeObject property7Object = componentNode2ShareableTypeDescriptor.newInstance();
        property7Object.set("configRef", "4ba1b6a0-9644-11e9-bc42-526af7764f64");

        GraphNode componentNode = new GenericComponentNode(componentData);
        componentData.set("property1", "first property");
        componentData.set("property7", property7Object);

        // When
        String actualJson = serialize(componentNode);

        // Then
        String expectedJson = WithTypeObjectReference.json();
        JSONAssert.assertEquals(expectedJson, actualJson, true);
    }

    // We expect that properties with not selected type object reference are not serialized.
    @Test
    void shouldCorrectlySerializeGenericComponentWithEmptyTypeObjectReference() {
        // Given
        ComponentData componentData = new ComponentData(ComponentDefaultDescriptor.create()
                .propertyDescriptors(asList(property1, property7))
                .fullyQualifiedName(ComponentNode1.class.getName())
                .build());

        TypeObjectDescriptor.TypeObject property7Object = componentNode2ShareableTypeDescriptor.newInstance();
        property7Object.set("configRef", "");

        GraphNode componentNode = new GenericComponentNode(componentData);
        componentData.set("property1", "first property");
        componentData.set("property7", property7Object);

        // When
        String actualJson = serialize(componentNode);

        // Then
        String expectedJson = WithTypeObjectReferenceMissing.json();
        JSONAssert.assertEquals(expectedJson, actualJson, true);
    }

    @Test
    void shouldCorrectlySerializeGenericComponentWithNotEmptyMapProperty() {
        // Given
        Map<String, Object> myMap = new HashMap<>();
        myMap.put("key1", "value1");
        myMap.put("key2", 3);

        ComponentData componentData = new ComponentData(ComponentDefaultDescriptor.create()
                .propertyDescriptors(asList(property1, property8))
                .fullyQualifiedName(ComponentNode1.class.getName())
                .build());

        GraphNode componentNode = new GenericComponentNode(componentData);
        componentData.set("property1", "first property");
        componentData.set("property8", myMap);

        // When
        String actualJson = serialize(componentNode);

        // Then
        String expectedJson = WithNotEmptyMapProperty.json();
        JSONAssert.assertEquals(expectedJson, actualJson, true);
    }

    @Test
    void shouldNotSerializeGenericComponentPropertyWithEmptyMap() {
        // Given
        Map<String, Object> myMap = new HashMap<>();

        ComponentData componentData = new ComponentData(ComponentDefaultDescriptor.create()
                .propertyDescriptors(asList(property1, property8))
                .fullyQualifiedName(ComponentNode1.class.getName())
                .build());

        GraphNode componentNode = new GenericComponentNode(componentData);
        componentData.set("property1", "first property");
        componentData.set("property8", myMap);

        // When
        String actualJson = serialize(componentNode);

        // Then
        String expectedJson = WithEmptyMapProperty.json();
        JSONAssert.assertEquals(expectedJson, actualJson, true);
    }

    private String serialize(GraphNode componentNode) {
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, componentNode);

        JSONArray sequence = new JSONArray();
        serializer.serialize(graph, sequence, componentNode, UNTIL_NO_SUCCESSORS);

        JSONObject serializedObject = sequence.getJSONObject(0);
        return serializedObject.toString(2);
    }
}
