package com.reedelk.plugin.component.type.generic;

import com.reedelk.module.descriptor.model.ComponentDescriptor;
import com.reedelk.module.descriptor.model.TypeObjectDescriptor;
import com.reedelk.plugin.commons.TypeObjectFactory;
import com.reedelk.plugin.component.ComponentData;
import com.reedelk.plugin.fixture.ComponentNode1;
import com.reedelk.plugin.fixture.Json.GenericComponent;
import com.reedelk.plugin.graph.node.GraphNode;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.util.ArrayList;
import java.util.List;

import static com.reedelk.plugin.component.type.generic.SamplePropertyDescriptors.Primitives.stringProperty;
import static com.reedelk.plugin.component.type.generic.SamplePropertyDescriptors.SpecialTypes.listProperty;
import static com.reedelk.plugin.component.type.generic.SamplePropertyDescriptors.SpecialTypes.listPropertyWithCustomValueType;
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
                .propertyDescriptors(asList(stringProperty, listProperty))
                .fullyQualifiedName(ComponentNode1.class.getName())
                .build());

        GraphNode componentNode = new GenericComponentNode(componentData);
        componentData.set("stringProperty", "first property");
        componentData.set("listProperty", myList);

        // When
        String actualJson = serialize(componentNode);

        // Then
        String expectedJson = GenericComponent.WithNotEmptyListProperty.json();
        JSONAssert.assertEquals(expectedJson, actualJson, true);
    }

    @Test
    void shouldNotSerializeGenericComponentPropertyWithEmptyList() {
        // Given
        List<Object> myList = new ArrayList<>();

        ComponentData componentData = new ComponentData(ComponentDescriptor.create()
                .propertyDescriptors(asList(stringProperty, listProperty))
                .fullyQualifiedName(ComponentNode1.class.getName())
                .build());

        GraphNode componentNode = new GenericComponentNode(componentData);
        componentData.set("stringProperty", "first property");
        componentData.set("listProperty", myList);

        // When
        String actualJson = serialize(componentNode);

        // Then
        String expectedJson = GenericComponent.WithEmptyListProperty.json();
        JSONAssert.assertEquals(expectedJson, actualJson, true);
    }

    @Test
    void shouldCorrectlySerializeGenericComponentWithNotEmptyListPropertyCustomValueType() {
        // Given
        TypeObjectDescriptor.TypeObject typeObjectInstance = TypeObjectFactory.newInstance();
        typeObjectInstance.set("stringProperty", "sample string property");
        typeObjectInstance.set("integerObjectProperty", Integer.parseInt("255"));

        CustomItemType item1 = new CustomItemType();
        item1.setStringProperty("200 string property");
        item1.setIntegerObjectProperty(200);

        CustomItemType item2 = new CustomItemType();
        item2.setStringProperty("400 string property");
        item2.setIntegerObjectProperty(400);

        List<CustomItemType> myList = new ArrayList<>();
        myList.add(item1);
        myList.add(item2);

        ComponentData componentData = new ComponentData(ComponentDescriptor.create()
                .propertyDescriptors(asList(stringProperty, listPropertyWithCustomValueType))
                .fullyQualifiedName(ComponentNode1.class.getName())
                .build());

        GraphNode componentNode = new GenericComponentNode(componentData);
        componentData.set("stringProperty", "first property");
        componentData.set("listPropertyWithCustomValueType", myList);

        // When
        String actualJson = serialize(componentNode);

        // Then
        String expectedJson = GenericComponent.WithNotEmptyListPropertyCustomValueType.json();
        JSONAssert.assertEquals(expectedJson, actualJson, true);
    }

    @Test
    void shouldNotSerializeGenericComponentPropertyWithEmptyListPropertyCustomValueType() {
        // Given
        List<CustomItemType> myList = new ArrayList<>();

        ComponentData componentData = new ComponentData(ComponentDescriptor.create()
                .propertyDescriptors(asList(stringProperty, listPropertyWithCustomValueType))
                .fullyQualifiedName(ComponentNode1.class.getName())
                .build());

        GraphNode componentNode = new GenericComponentNode(componentData);
        componentData.set("stringProperty", "first property");
        componentData.set("listPropertyWithCustomValueType", myList);

        // When
        String actualJson = serialize(componentNode);

        // Then
        String expectedJson = GenericComponent.WithEmptyListPropertyCustomValueType.json();
        JSONAssert.assertEquals(expectedJson, actualJson, true);
    }
}
