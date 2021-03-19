package de.codecentric.reedelk.plugin.component.type.generic;

import de.codecentric.reedelk.module.descriptor.model.component.ComponentDescriptor;
import de.codecentric.reedelk.plugin.commons.TypeObjectFactory;
import de.codecentric.reedelk.plugin.component.ComponentData;
import de.codecentric.reedelk.plugin.fixture.ComponentNode1;
import de.codecentric.reedelk.plugin.fixture.Json;
import de.codecentric.reedelk.plugin.graph.node.GraphNode;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import static de.codecentric.reedelk.module.descriptor.model.property.ObjectDescriptor.TypeObject;
import static java.util.Arrays.asList;

public class GenericComponentSerializerTypeObjectTest extends GenericComponentSerializerBaseTest {

    @Test
    void shouldCorrectlySerializeTypeObject() {
        // Given
        ComponentData componentData = new ComponentData(ComponentDescriptor.create()
                .properties(asList(SamplePropertyDescriptors.Primitives.stringProperty, SamplePropertyDescriptors.TypeObjects.typeObjectProperty))
                .fullyQualifiedName(ComponentNode1.class.getName())
                .build());

        TypeObject typeObjectInstance = TypeObjectFactory.newInstance();
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
                .properties(asList(SamplePropertyDescriptors.Primitives.booleanProperty, SamplePropertyDescriptors.TypeObjects.typeObjectSharedProperty))
                .fullyQualifiedName(ComponentNode1.class.getName())
                .build());

        TypeObject typeObjectSharedInstance = TypeObjectFactory.newInstance();
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
                .properties(asList(SamplePropertyDescriptors.Primitives.booleanProperty, SamplePropertyDescriptors.TypeObjects.typeObjectSharedProperty))
                .fullyQualifiedName(ComponentNode1.class.getName())
                .build());

        TypeObject typeObjectSharedInstance = TypeObjectFactory.newInstance();
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
