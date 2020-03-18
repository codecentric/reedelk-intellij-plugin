package com.reedelk.plugin.component.type.generic;

import com.reedelk.module.descriptor.model.ComponentDescriptor;
import com.reedelk.plugin.component.ComponentData;
import com.reedelk.plugin.fixture.ComponentNode1;
import com.reedelk.plugin.fixture.Json;
import com.reedelk.plugin.graph.node.GraphNode;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.math.BigDecimal;
import java.math.BigInteger;

import static com.reedelk.plugin.component.type.generic.SamplePropertyDescriptors.Primitives;
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
            .propertyDescriptors(asList(
                    Primitives.integerProperty,
                    Primitives.integerObjectProperty,
                    Primitives.longProperty,
                    Primitives.longObjectProperty,
                    Primitives.floatProperty,
                    Primitives.floatObjectProperty,
                    Primitives.doubleProperty,
                    Primitives.doubleObjectProperty,
                    Primitives.booleanProperty,
                    Primitives.booleanObjectProperty,
                    Primitives.stringProperty,
                    Primitives.bigIntegerProperty,
                    Primitives.bigDecimalProperty))
            .fullyQualifiedName(ComponentNode1.class.getName())
            .build();
}
