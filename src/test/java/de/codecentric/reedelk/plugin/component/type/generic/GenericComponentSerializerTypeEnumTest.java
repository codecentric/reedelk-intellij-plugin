package de.codecentric.reedelk.plugin.component.type.generic;

import de.codecentric.reedelk.module.descriptor.model.component.ComponentDescriptor;
import de.codecentric.reedelk.plugin.component.ComponentData;
import de.codecentric.reedelk.plugin.fixture.ComponentNode1;
import de.codecentric.reedelk.plugin.fixture.Json;
import de.codecentric.reedelk.plugin.graph.node.GraphNode;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import static java.util.Arrays.asList;

public class GenericComponentSerializerTypeEnumTest extends GenericComponentSerializerBaseTest {

    @Test
    void shouldCorrectlySerializeGenericComponentWithEnumProperty() {
        // Given
        ComponentData componentData = new ComponentData(ComponentDescriptor.create()
                .properties(asList(SamplePropertyDescriptors.Primitives.floatProperty, SamplePropertyDescriptors.SpecialTypes.enumProperty))
                .fullyQualifiedName(ComponentNode1.class.getName())
                .build());

        GraphNode componentNode = new GenericComponentNode(componentData);
        componentData.set("floatProperty", 2483.002f);
        componentData.set("enumProperty", "CERT");

        // When
        String actualJson = serialize(componentNode);

        // Then
        String expectedJson = Json.GenericComponent.WithEnumProperty.json();
        JSONAssert.assertEquals(expectedJson, actualJson, true);
    }
}
