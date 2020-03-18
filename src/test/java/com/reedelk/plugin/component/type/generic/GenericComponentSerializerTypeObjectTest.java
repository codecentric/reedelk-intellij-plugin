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

import static com.reedelk.plugin.component.type.generic.SamplePropertyDescriptors.Primitives;
import static com.reedelk.plugin.component.type.generic.SamplePropertyDescriptors.Primitives.stringProperty;
import static com.reedelk.plugin.component.type.generic.SamplePropertyDescriptors.TypeObjects;
import static java.util.Arrays.asList;

public class GenericComponentSerializerTypeObjectTest extends GenericComponentSerializerBaseTest {

    @Test
    void shouldCorrectlySerializeTypeObject() {
        // Given
        ComponentData componentData = new ComponentData(ComponentDescriptor.create()
                .propertyDescriptors(asList(stringProperty, TypeObjects.typeObjectProperty))
                .fullyQualifiedName(ComponentNode1.class.getName())
                .build());

        TypeObjectDescriptor.TypeObject typeObjectInstance = TypeObjectFactory.newInstance();
        typeObjectInstance.set("stringProperty", "sample string property");
        typeObjectInstance.set("integerObjectProperty", Integer.parseInt("255"));

        GraphNode componentNode = new GenericComponentNode(componentData);
        componentData.set("stringProperty", "yet another string property");
        componentData.set("typeObjectProperty", typeObjectInstance);

        // When
        String actualJson = serialize(componentNode);

        // Then
        String expectedJson = Json.GenericComponent.WithTypeObject.json();
        JSONAssert.assertEquals(expectedJson, actualJson, true);
    }

    @Test
    void shouldCorrectlySerializeGenericComponentWithTypeObjectReference() {
        // Given
        ComponentData componentData = new ComponentData(ComponentDescriptor.create()
                .propertyDescriptors(asList(Primitives.booleanProperty, TypeObjects.typeObjectSharedProperty))
                .fullyQualifiedName(ComponentNode1.class.getName())
                .build());

        TypeObjectDescriptor.TypeObject typeObjectSharedInstance = TypeObjectFactory.newInstance();
        typeObjectSharedInstance.set("ref", "4ba1b6a0-9644-11e9-bc42-526af7764f64");

        GraphNode componentNode = new GenericComponentNode(componentData);
        componentData.set("booleanProperty", true);
        componentData.set("typeObjectSharedProperty", typeObjectSharedInstance);

        // When
        String actualJson = serialize(componentNode);

        // Then
        String expectedJson = Json.GenericComponent.WithTypeObjectReference.json();
        JSONAssert.assertEquals(expectedJson, actualJson, true);
    }

    // We expect that properties with an empty config reference are not serialized.
    @Test
    void shouldCorrectlySerializeGenericComponentWithEmptyTypeObjectReference() {
        // Given
        ComponentData componentData = new ComponentData(ComponentDescriptor.create()
                .propertyDescriptors(asList(Primitives.booleanProperty, TypeObjects.typeObjectSharedProperty))
                .fullyQualifiedName(ComponentNode1.class.getName())
                .build());

        TypeObjectDescriptor.TypeObject typeObjectSharedInstance = TypeObjectFactory.newInstance();
        typeObjectSharedInstance.set("ref", "");

        GraphNode componentNode = new GenericComponentNode(componentData);
        componentData.set("booleanProperty", true);
        componentData.set("typeObjectSharedProperty", typeObjectSharedInstance);

        // When
        String actualJson = serialize(componentNode);

        // Then
        String expectedJson = Json.GenericComponent.WithTypeObjectMissing.json();
        JSONAssert.assertEquals(expectedJson, actualJson, true);
    }
}
