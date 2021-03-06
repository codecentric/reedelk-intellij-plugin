package de.codecentric.reedelk.plugin.component.type.generic;

import de.codecentric.reedelk.module.descriptor.model.component.ComponentDescriptor;
import de.codecentric.reedelk.plugin.commons.TypeObjectFactory;
import de.codecentric.reedelk.plugin.component.ComponentData;
import de.codecentric.reedelk.plugin.fixture.ComponentNode1;
import de.codecentric.reedelk.plugin.fixture.Json;
import de.codecentric.reedelk.plugin.graph.node.GraphNode;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.util.ArrayList;
import java.util.List;

import static de.codecentric.reedelk.module.descriptor.model.property.ObjectDescriptor.TypeObject;
import static de.codecentric.reedelk.module.descriptor.model.property.ObjectDescriptor.newInstance;
import static java.util.Arrays.asList;

public class GenericComponentSerializerTypeListTest extends GenericComponentSerializerBaseTest {

    @Test
    void shouldCorrectlySerializeGenericComponentWithNotEmptyListProperty() {
        // Given
        List<Object> myList = new ArrayList<>();
        myList.add("one");
        myList.add("two");
        myList.add("three");

        ComponentData componentData = new ComponentData(ComponentDescriptor.create()
                .properties(asList(SamplePropertyDescriptors.Primitives.stringProperty, SamplePropertyDescriptors.SpecialTypes.listProperty))
                .fullyQualifiedName(ComponentNode1.class.getName())
                .build());

        GraphNode componentNode = new GenericComponentNode(componentData);
        componentData.set("stringProperty", "first property");
        componentData.set("listProperty", myList);

        // When
        String actualJson = serialize(componentNode);

        // Then
        String expectedJson = Json.GenericComponent.WithNotEmptyListProperty.json();
        JSONAssert.assertEquals(expectedJson, actualJson, true);
    }

    @Test
    void shouldNotSerializeGenericComponentPropertyWithEmptyList() {
        // Given
        List<Object> myList = new ArrayList<>();

        ComponentData componentData = new ComponentData(ComponentDescriptor.create()
                .properties(asList(SamplePropertyDescriptors.Primitives.stringProperty, SamplePropertyDescriptors.SpecialTypes.listProperty))
                .fullyQualifiedName(ComponentNode1.class.getName())
                .build());

        GraphNode componentNode = new GenericComponentNode(componentData);
        componentData.set("stringProperty", "first property");
        componentData.set("listProperty", myList);

        // When
        String actualJson = serialize(componentNode);

        // Then
        String expectedJson = Json.GenericComponent.WithEmptyListProperty.json();
        JSONAssert.assertEquals(expectedJson, actualJson, true);
    }

    @Test
    void shouldCorrectlySerializeGenericComponentWithNotEmptyListPropertyCustomValueType() {
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

        List<TypeObject> myList = new ArrayList<>();
        myList.add(item1);
        myList.add(item2);

        ComponentData componentData = new ComponentData(ComponentDescriptor.create()
                .properties(asList(SamplePropertyDescriptors.Primitives.stringProperty, SamplePropertyDescriptors.SpecialTypes.listPropertyWithCustomValueType))
                .fullyQualifiedName(ComponentNode1.class.getName())
                .build());

        GraphNode componentNode = new GenericComponentNode(componentData);
        componentData.set("stringProperty", "first property");
        componentData.set("listPropertyWithCustomValueType", myList);

        // When
        String actualJson = serialize(componentNode);

        // Then
        String expectedJson = Json.GenericComponent.WithNotEmptyListPropertyCustomValueType.json();
        JSONAssert.assertEquals(expectedJson, actualJson, true);
    }

    @Test
    void shouldNotSerializeGenericComponentPropertyWithEmptyListPropertyCustomValueType() {
        // Given
        List<TypeObject> myList = new ArrayList<>();

        ComponentData componentData = new ComponentData(ComponentDescriptor.create()
                .properties(asList(SamplePropertyDescriptors.Primitives.stringProperty, SamplePropertyDescriptors.SpecialTypes.listPropertyWithCustomValueType))
                .fullyQualifiedName(ComponentNode1.class.getName())
                .build());

        GraphNode componentNode = new GenericComponentNode(componentData);
        componentData.set("stringProperty", "first property");
        componentData.set("listPropertyWithCustomValueType", myList);

        // When
        String actualJson = serialize(componentNode);

        // Then
        String expectedJson = Json.GenericComponent.WithEmptyListPropertyCustomValueType.json();
        JSONAssert.assertEquals(expectedJson, actualJson, true);
    }
}
