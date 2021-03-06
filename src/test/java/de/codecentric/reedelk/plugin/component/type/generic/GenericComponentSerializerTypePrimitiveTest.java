package de.codecentric.reedelk.plugin.component.type.generic;

import de.codecentric.reedelk.module.descriptor.model.component.ComponentDescriptor;
import de.codecentric.reedelk.plugin.component.ComponentData;
import de.codecentric.reedelk.plugin.fixture.ComponentNode1;
import de.codecentric.reedelk.plugin.fixture.Json;
import de.codecentric.reedelk.plugin.graph.node.GraphNode;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.math.BigDecimal;
import java.math.BigInteger;

import static java.util.Arrays.asList;

public class GenericComponentSerializerTypePrimitiveTest extends GenericComponentSerializerBaseTest {

    @Test
    void shouldCorrectlySerializePrimitiveTypesValues() {
        // Given
        ComponentData componentData = new ComponentData(descriptor);
        componentData.set("integerProperty", 234923);
        componentData.set("integerObjectProperty", Integer.parseInt("998829743"));
        componentData.set("longProperty", 913281L);
        componentData.set("longObjectProperty", Long.parseLong("55663"));
        componentData.set("floatProperty", 123.234f);
        componentData.set("floatObjectProperty", Float.parseFloat("7843.12"));
        componentData.set("doubleProperty", 234.234d);
        componentData.set("doubleObjectProperty", Double.parseDouble("11.88877"));
        componentData.set("booleanProperty", true);
        componentData.set("booleanObjectProperty", Boolean.TRUE);
        componentData.set("stringProperty", "my text sample");
        componentData.set("bigIntegerProperty", new BigInteger("88923423423"));
        componentData.set("bigDecimalProperty", new BigDecimal("1.001"));

        GraphNode componentNode = new GenericComponentNode(componentData);

        // When
        String actualJson = serialize(componentNode);

        // Then
        String expectedJson = Json.GenericComponent.Primitives.json();
        JSONAssert.assertEquals(expectedJson, actualJson, true);
    }

    @Test
    void shouldCorrectlySkipSerializationOfNullPrimitiveTypesValues() {
        // Given
        ComponentData componentData = new ComponentData(descriptor);
        GraphNode componentNode = new GenericComponentNode(componentData);

        // When
        String actualJson = serialize(componentNode);

        // Then
        String expectedJson = Json.GenericComponent.PrimitivesNull.json();
        JSONAssert.assertEquals(expectedJson, actualJson, true);
    }

    ComponentDescriptor descriptor = ComponentDescriptor.create()
            .properties(asList(
                    SamplePropertyDescriptors.Primitives.integerProperty,
                    SamplePropertyDescriptors.Primitives.integerObjectProperty,
                    SamplePropertyDescriptors.Primitives.longProperty,
                    SamplePropertyDescriptors.Primitives.longObjectProperty,
                    SamplePropertyDescriptors.Primitives.floatProperty,
                    SamplePropertyDescriptors.Primitives.floatObjectProperty,
                    SamplePropertyDescriptors.Primitives.doubleProperty,
                    SamplePropertyDescriptors.Primitives.doubleObjectProperty,
                    SamplePropertyDescriptors.Primitives.booleanProperty,
                    SamplePropertyDescriptors.Primitives.booleanObjectProperty,
                    SamplePropertyDescriptors.Primitives.stringProperty,
                    SamplePropertyDescriptors.Primitives.bigIntegerProperty,
                    SamplePropertyDescriptors.Primitives.bigDecimalProperty))
            .fullyQualifiedName(ComponentNode1.class.getName())
            .build();
}
