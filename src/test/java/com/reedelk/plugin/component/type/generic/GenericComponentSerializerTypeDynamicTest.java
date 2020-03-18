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

import static java.util.Arrays.asList;

public class GenericComponentSerializerTypeDynamicTest extends GenericComponentSerializerBaseTest {

    @Test
    void shouldCorrectlySerializeDynamicTypeProperties() {
        // Given
        ComponentData componentData = new ComponentData(descriptor);
        componentData.set("dynamicBigDecimalProperty", new BigDecimal("44.001"));
        componentData.set("dynamicBigIntegerProperty", new BigInteger("8811823843"));
        componentData.set("dynamicBooleanProperty", Boolean.TRUE);
        componentData.set("dynamicByteArrayProperty", "byte array string");
        componentData.set("dynamicDoubleProperty", Double.parseDouble("4523.234"));
        componentData.set("dynamicFloatProperty", Float.parseFloat("7843.12"));
        componentData.set("dynamicIntegerProperty", Integer.parseInt("3"));
        componentData.set("dynamicLongProperty", Long.parseLong("99933322"));
        componentData.set("dynamicObjectProperty", "my object string");
        componentData.set("dynamicStringProperty", "my dynamic string");

        GraphNode componentNode = new GenericComponentNode(componentData);

        // When
        String actualJson = serialize(componentNode);

        // Then
        String expectedJson = Json.GenericComponent.DynamicTypes.json();
        JSONAssert.assertEquals(expectedJson, actualJson, true);
    }

    private final ComponentDescriptor descriptor = ComponentDescriptor.create()
            .propertyDescriptors(asList(
                    SamplePropertyDescriptors.DynamicTypes.dynamicBigDecimalProperty,
                    SamplePropertyDescriptors.DynamicTypes.dynamicBigIntegerProperty,
                    SamplePropertyDescriptors.DynamicTypes.dynamicBooleanProperty,
                    SamplePropertyDescriptors.DynamicTypes.dynamicByteArrayProperty,
                    SamplePropertyDescriptors.DynamicTypes.dynamicDoubleProperty,
                    SamplePropertyDescriptors.DynamicTypes.dynamicFloatProperty,
                    SamplePropertyDescriptors.DynamicTypes.dynamicIntegerProperty,
                    SamplePropertyDescriptors.DynamicTypes.dynamicLongProperty,
                    SamplePropertyDescriptors.DynamicTypes.dynamicObjectProperty,
                    SamplePropertyDescriptors.DynamicTypes.dynamicStringProperty))
            .fullyQualifiedName(ComponentNode1.class.getName())
            .build();
}
