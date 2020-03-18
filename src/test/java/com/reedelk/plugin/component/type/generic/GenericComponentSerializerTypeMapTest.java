package com.reedelk.plugin.component.type.generic;

import com.reedelk.module.descriptor.model.ComponentDescriptor;
import com.reedelk.module.descriptor.model.TypeObjectDescriptor;
import com.reedelk.plugin.commons.TypeObjectFactory;
import com.reedelk.plugin.component.ComponentData;
import com.reedelk.plugin.fixture.ComponentNode1;
import com.reedelk.plugin.fixture.Json;
import com.reedelk.plugin.graph.node.GraphNode;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.util.HashMap;
import java.util.Map;

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
                .propertyDescriptors(asList(Primitives.stringProperty, SpecialTypes.mapProperty))
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
                .propertyDescriptors(asList(Primitives.stringProperty, SpecialTypes.mapProperty))
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
        TypeObjectDescriptor.TypeObject typeObjectInstance = TypeObjectFactory.newInstance();
        typeObjectInstance.set("stringProperty", "sample string property");
        typeObjectInstance.set("integerObjectProperty", Integer.parseInt("255"));

        CustomMapValueType mapValueType1 = new CustomMapValueType();
        mapValueType1.setStringProperty("200 string property");
        mapValueType1.setIntegerObjectProperty(200);

        CustomMapValueType mapValueType2 = new CustomMapValueType();
        mapValueType2.setStringProperty("400 string property");
        mapValueType2.setIntegerObjectProperty(400);

        Map<String, CustomMapValueType> myMap = new HashMap<>();
        myMap.put("200", mapValueType1);
        myMap.put("400", mapValueType2);

        ComponentData componentData = new ComponentData(ComponentDescriptor.create()
                .propertyDescriptors(asList(Primitives.stringProperty, SpecialTypes.mapPropertyWithCustomValueType))
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
        Map<String, CustomMapValueType> myMap = new HashMap<>();

        ComponentData componentData = new ComponentData(ComponentDescriptor.create()
                .propertyDescriptors(asList(Primitives.stringProperty, SpecialTypes.mapPropertyWithCustomValueType))
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
