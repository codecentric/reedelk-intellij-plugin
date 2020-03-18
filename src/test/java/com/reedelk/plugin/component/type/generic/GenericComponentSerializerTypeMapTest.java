package com.reedelk.plugin.component.type.generic;

import com.reedelk.module.descriptor.model.ComponentDescriptor;
import com.reedelk.plugin.component.ComponentData;
import com.reedelk.plugin.fixture.ComponentNode1;
import com.reedelk.plugin.fixture.Json;
import com.reedelk.plugin.graph.node.GraphNode;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.util.HashMap;
import java.util.Map;

import static java.util.Arrays.asList;

public class GenericComponentSerializerTypeMapTest extends GenericComponentSerializerBaseTest {

    @Test
    void shouldCorrectlySerializeGenericComponentWithNotEmptyMapProperty() {
        // Given
        Map<String, Object> myMap = new HashMap<>();
        myMap.put("key1", "value1");
        myMap.put("key2", "value2");

        ComponentData componentData = new ComponentData(ComponentDescriptor.create()
                .propertyDescriptors(asList(SamplePropertyDescriptors.Primitives.stringProperty, SamplePropertyDescriptors.SpecialTypes.mapProperty))
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
                .propertyDescriptors(asList(SamplePropertyDescriptors.Primitives.stringProperty, SamplePropertyDescriptors.SpecialTypes.mapProperty))
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
}
