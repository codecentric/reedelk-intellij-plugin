package com.reedelk.plugin.component.type.generic;

import com.reedelk.module.descriptor.model.component.ComponentDescriptor;
import com.reedelk.plugin.commons.TypeObjectFactory;
import com.reedelk.plugin.component.ComponentData;
import com.reedelk.plugin.fixture.ComponentNode1;
import com.reedelk.plugin.fixture.Json;
import com.reedelk.plugin.graph.node.GraphNode;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.util.HashMap;
import java.util.Map;

import static com.reedelk.module.descriptor.model.property.ObjectDescriptor.TypeObject;
import static com.reedelk.module.descriptor.model.property.ObjectDescriptor.newInstance;
import static com.reedelk.plugin.component.type.generic.SamplePropertyDescriptors.Primitives;
import static com.reedelk.plugin.component.type.generic.SamplePropertyDescriptors.SpecialTypes;
import static java.util.Arrays.asList;

public class GenericComponentSerializerTypeMapTest extends GenericComponentSerializerBaseTest {

    @Test
    void shouldCorrectlySerializeGenericComponentWithNotEmptyMapProperty() {
        // Given
        Map<String, Object> myMap = new HashMap<>();
        myMap.put("key1", "value1");
        myMap.put("key2", "value2");

        ComponentData componentData = new ComponentData(ComponentDescriptor.create()
                .properties(asList(Primitives.stringProperty, SpecialTypes.mapProperty))
                .fullyQualifiedName(ComponentNode1.class.getName())
                .build());

        GraphNode componentNode = new GenericComponentNode(componentData);
        componentData.set("stringProperty", "first property");
        componentData.set("mapProperty", myMap);

        // When
        String actualJson = serialize(componentNode);

        // Then
        String expectedJson = Json.GenericComponent.WithNotEmptyMapProperty.json();
        JSONAssert.assertEquals(expectedJson, actualJson, true);
    }

    @Test
    void shouldNotSerializeGenericComponentPropertyWithEmptyMap() {
        // Given
        Map<String, Object> myMap = new HashMap<>();

        ComponentData componentData = new ComponentData(ComponentDescriptor.create()
                .properties(asList(Primitives.stringProperty, SpecialTypes.mapProperty))
                .fullyQualifiedName(ComponentNode1.class.getName())
                .build());

        GraphNode componentNode = new GenericComponentNode(componentData);
        componentData.set("stringProperty", "first property");
        componentData.set("mapProperty", myMap);

        // When
        String actualJson = serialize(componentNode);

        // Then
        String expectedJson = Json.GenericComponent.WithEmptyMapProperty.json();
        JSONAssert.assertEquals(expectedJson, actualJson, true);
    }

    @Test
    void shouldCorrectlySerializeGenericComponentWithNotEmptyMapPropertyCustomValueType() {
        // Given
        TypeObject typeObjectInstance = TypeObjectFactory.newInstance();
        typeObjectInstance.set("stringProperty", "sample string property");
        typeObjectInstance.set("integerObjectProperty", Integer.parseInt("255"));

        TypeObject item1 = newInstance();
        item1.set("stringProperty", "200 string property");
        item1.set("integerObjectProperty", 200);

        TypeObject item2 = newInstance();
        item2.set("stringProperty", "400 string property");
        item2.set("integerObjectProperty", 400);

        Map<String, TypeObject> myMap = new HashMap<>();
        myMap.put("200", item1);
        myMap.put("400", item2);

        ComponentData componentData = new ComponentData(ComponentDescriptor.create()
                .properties(asList(Primitives.stringProperty, SpecialTypes.mapPropertyWithCustomValueType))
                .fullyQualifiedName(ComponentNode1.class.getName())
                .build());

        GraphNode componentNode = new GenericComponentNode(componentData);
        componentData.set("stringProperty", "first property");
        componentData.set("mapPropertyWithCustomValueType", myMap);

        // When
        String actualJson = serialize(componentNode);

        // Then
        String expectedJson = Json.GenericComponent.WithNotEmptyMapPropertyCustomValueType.json();
        JSONAssert.assertEquals(expectedJson, actualJson, true);
    }

    @Test
    void shouldNotSerializeGenericComponentPropertyWithEmptyMapPropertyCustomValueType() {
        // Given
        Map<String, TypeObject> myMap = new HashMap<>();

        ComponentData componentData = new ComponentData(ComponentDescriptor.create()
                .properties(asList(Primitives.stringProperty, SpecialTypes.mapPropertyWithCustomValueType))
                .fullyQualifiedName(ComponentNode1.class.getName())
                .build());

        GraphNode componentNode = new GenericComponentNode(componentData);
        componentData.set("stringProperty", "first property");
        componentData.set("mapPropertyWithCustomValueType", myMap);

        // When
        String actualJson = serialize(componentNode);

        // Then
        String expectedJson = Json.GenericComponent.WithEmptyMapPropertyCustomValueType.json();
        JSONAssert.assertEquals(expectedJson, actualJson, true);
    }
}
