package com.reedelk.plugin.component.type.generic;

import com.reedelk.module.descriptor.model.component.ComponentDescriptor;
import com.reedelk.plugin.assertion.PluginAssertion;
import com.reedelk.plugin.fixture.ComponentNode1;
import com.reedelk.plugin.fixture.Json;
import com.reedelk.plugin.graph.deserializer.AbstractNodeDeserializerTest;
import com.reedelk.plugin.graph.node.GraphNode;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

import static java.util.Arrays.asList;

public class GenericComponentDeserializerTypeDynamicTest extends AbstractNodeDeserializerTest {

    @Test
    void shouldCorrectlyDeserializeDynamicTypeProperties() {
        // Given
        GenericComponentNode node = createGraphNodeInstance(GenericComponentNode.class, descriptor);

        mockContextInstantiateGraphNode(node);

        JSONObject genericComponentDefinition = new JSONObject(Json.GenericComponent.DynamicTypes.json());

        // When
        GenericComponentDeserializer deserializer = new GenericComponentDeserializer(graph, node, context);
        GraphNode lastNode = deserializer.deserialize(root, genericComponentDefinition);

        // Then
        PluginAssertion.assertThat(graph)
                .node(lastNode).is(node)
                .hasDataWithValue("dynamicBigDecimalProperty", new BigDecimal(44.001))
                .hasDataWithValue("dynamicBigIntegerProperty", new BigInteger("8811823843"))
                .hasDataWithValue("dynamicBooleanProperty", Boolean.TRUE)
                .hasDataWithValue("dynamicByteArrayProperty", "byte array string")
                .hasDataWithValue("dynamicDoubleProperty", Double.parseDouble("4523.234"))
                .hasDataWithValue("dynamicFloatProperty", Float.parseFloat("7843.12"))
                .hasDataWithValue("dynamicIntegerProperty", Integer.valueOf("3"))
                .hasDataWithValue("dynamicLongProperty", Long.valueOf("99933322"))
                .hasDataWithValue("dynamicObjectProperty", "my object string")
                .hasDataWithValue("dynamicStringProperty", "my dynamic string")
                .and().nodesCountIs(2);
    }

    @Test
    void shouldCorrectlyDeserializeDynamicTypePropertiesWithScript() {
        // Given
        GenericComponentNode node = createGraphNodeInstance(GenericComponentNode.class, descriptor);

        mockContextInstantiateGraphNode(node);

        JSONObject genericComponentDefinition = new JSONObject(Json.GenericComponent.DynamicTypesWithScript.json());

        // When
        GenericComponentDeserializer deserializer = new GenericComponentDeserializer(graph, node, context);
        GraphNode lastNode = deserializer.deserialize(root, genericComponentDefinition);

        // Then
        PluginAssertion.assertThat(graph)
                .node(lastNode).is(node)
                .hasDataWithValue("dynamicBigDecimalProperty", "#['big decimal']")
                .hasDataWithValue("dynamicBigIntegerProperty", "#['big integer']")
                .hasDataWithValue("dynamicBooleanProperty", "#['boolean']")
                .hasDataWithValue("dynamicByteArrayProperty", "#['byte array']")
                .hasDataWithValue("dynamicDoubleProperty", "#['double']")
                .hasDataWithValue("dynamicFloatProperty", "#['float']")
                .hasDataWithValue("dynamicIntegerProperty", "#['integer']")
                .hasDataWithValue("dynamicLongProperty", "#['long']")
                .hasDataWithValue("dynamicObjectProperty", "#['object']")
                .hasDataWithValue("dynamicStringProperty", "#['string']")
                .and().nodesCountIs(2);
    }

    ComponentDescriptor descriptor = ComponentDescriptor.create()
            .properties(asList(
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
