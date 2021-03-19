package de.codecentric.reedelk.plugin.component.type.generic;

import de.codecentric.reedelk.module.descriptor.model.component.ComponentDescriptor;
import de.codecentric.reedelk.plugin.commons.TypeObjectFactory;
import de.codecentric.reedelk.plugin.component.ComponentData;
import de.codecentric.reedelk.plugin.fixture.ComponentNode1;
import de.codecentric.reedelk.plugin.fixture.Json;
import de.codecentric.reedelk.plugin.graph.node.GraphNode;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.util.HashMap;
import java.util.Map;

import static de.codecentric.reedelk.module.descriptor.model.property.ObjectDescriptor.TypeObject;
import static de.codecentric.reedelk.module.descriptor.model.property.ObjectDescriptor.newInstance;
import static java.util.Arrays.asList;

public class GenericComponentSerializerTypeMapTest extends GenericComponentSerializerBaseTest {

    @Test
    void shouldCorrectlySerializeGenericComponentWithNotEmptyMapProperty() {
        // Given
        Map<String, Object> myMap = new HashMap<>();
        myMap.put("key1", "value1");
        myMap.put("key2", "value2");

        ComponentData componentData = new ComponentData(ComponentDescriptor.create()
                .properties(asList(SamplePropertyDescriptors.Primitives.stringProperty, SamplePropertyDescriptors.SpecialTypes.mapProperty))
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
                .properties(asList(SamplePropertyDescriptors.Primitives.stringProperty, SamplePropertyDescriptors.SpecialTypes.mapProperty))
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
                .properties(asList(SamplePropertyDescriptors.Primitives.stringProperty, SamplePropertyDescriptors.SpecialTypes.mapPropertyWithCustomValueType))
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
                .properties(asList(SamplePropertyDescriptors.Primitives.stringProperty, SamplePropertyDescriptors.SpecialTypes.mapPropertyWithCustomValueType))
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
