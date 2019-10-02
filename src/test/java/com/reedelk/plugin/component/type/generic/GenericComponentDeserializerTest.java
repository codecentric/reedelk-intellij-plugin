package com.reedelk.plugin.component.type.generic;

import com.reedelk.plugin.assertion.PluginAssertion;
import com.reedelk.plugin.component.domain.ComponentDefaultDescriptor;
import com.reedelk.plugin.component.domain.ComponentDescriptor;
import com.reedelk.plugin.fixture.ComponentNode1;
import com.reedelk.plugin.graph.deserializer.AbstractNodeDeserializerTest;
import com.reedelk.plugin.graph.node.GraphNode;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.math.BigInteger;

import static com.reedelk.plugin.component.type.generic.SamplePropertyDescriptors.DynamicTypes;
import static com.reedelk.plugin.component.type.generic.SamplePropertyDescriptors.Primitives;
import static com.reedelk.plugin.fixture.Json.GenericComponent;
import static java.util.Arrays.asList;

@MockitoSettings(strictness = Strictness.LENIENT)
class GenericComponentDeserializerTest extends AbstractNodeDeserializerTest {

    private GenericComponentDeserializer deserializer;

    @BeforeEach
    protected void setUp() {
        super.setUp();
        deserializer = new GenericComponentDeserializer(graph, context);
    }

    @Nested
    @DisplayName("Component primitives types are de-serialized correctly")
    class PrimitiveTypeDeserialization {

        private ComponentDescriptor descriptor = ComponentDefaultDescriptor.create()
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

        @Test
        void shouldCorrectlyDeserializePrimitiveTypesValues() {
            // Given
            GenericComponentNode node = createGraphNodeInstance(GenericComponentNode.class, descriptor);

            mockContextInstantiateGraphNode(node);

            JSONObject genericComponentDefinition = new JSONObject(GenericComponent.Primitives.json());

            // When
            GraphNode lastNode = deserializer.deserialize(root, genericComponentDefinition);

            // Then
            PluginAssertion.assertThat(graph)
                    .node(lastNode).is(node)
                    .hasDataWithValue("integerProperty", 234923)
                    .hasDataWithValue("integerObjectProperty", new Integer("998829743"))
                    .hasDataWithValue("longProperty", 913281L)
                    .hasDataWithValue("longObjectProperty", new Long("55663"))
                    .hasDataWithValue("floatProperty", 123.234f)
                    .hasDataWithValue("floatObjectProperty", new Float("7843.12"))
                    .hasDataWithValue("doubleProperty", 234.234d)
                    .hasDataWithValue("doubleObjectProperty", new Double("11.88877"))
                    .hasDataWithValue("booleanProperty", true)
                    .hasDataWithValue("booleanObjectProperty", Boolean.FALSE)
                    .hasDataWithValue("stringProperty", "my text sample")
                    .hasDataWithValue("bigIntegerProperty", new BigInteger("88923423423"))
                    .hasDataWithValue("bigDecimalProperty", new BigDecimal("1.001"))
                    .and().nodesCountIs(2);
        }
    }

    @Nested
    @DisplayName("Component Dynamic properties are de-serialized correctly")
    class TypeDynamicPropertiesDeserialization {

        ComponentDescriptor descriptor = ComponentDefaultDescriptor.create()
                .propertyDescriptors(asList(
                        DynamicTypes.dynamicBigDecimalProperty,
                        DynamicTypes.dynamicBigIntegerProperty,
                        DynamicTypes.dynamicBooleanProperty,
                        DynamicTypes.dynamicByteArrayProperty,
                        DynamicTypes.dynamicDoubleProperty,
                        DynamicTypes.dynamicFloatProperty,
                        DynamicTypes.dynamicIntegerProperty,
                        DynamicTypes.dynamicLongProperty,
                        DynamicTypes.dynamicObjectProperty,
                        DynamicTypes.dynamicStringProperty))
                .fullyQualifiedName(ComponentNode1.class.getName())
                .build();

        @Test
        void shouldCorrectlyDeserializeDynamicTypeProperties() {
            // Given
            GenericComponentNode node = createGraphNodeInstance(GenericComponentNode.class, descriptor);

            mockContextInstantiateGraphNode(node);

            JSONObject genericComponentDefinition = new JSONObject(GenericComponent.DynamicTypes.json());

            // When
            GraphNode lastNode = deserializer.deserialize(root, genericComponentDefinition);

            // Then
            PluginAssertion.assertThat(graph)
                    .node(lastNode).is(node)
                    .hasDataWithValue("dynamicBigDecimalProperty", new BigDecimal("44.001"))
                    .hasDataWithValue("dynamicBigIntegerProperty", new BigInteger("8811823843"))
                    .hasDataWithValue("dynamicBooleanProperty", Boolean.TRUE)
                    .hasDataWithValue("dynamicByteArrayProperty", "byte array string")
                    .hasDataWithValue("dynamicDoubleProperty", new Double("4523.234"))
                    .hasDataWithValue("dynamicFloatProperty", new Float("7843.12"))
                    .hasDataWithValue("dynamicIntegerProperty", new Integer("3"))
                    .hasDataWithValue("dynamicLongProperty", new Long("99933322"))
                    .hasDataWithValue("dynamicObjectProperty", "my object string")
                    .hasDataWithValue("dynamicStringProperty", "my dynamic string")
                    .and().nodesCountIs(2);
        }

        @Test
        void shouldCorrectlyDeserializeDynamicTypePropertiesWithScript() {
            // Given
            GenericComponentNode node = createGraphNodeInstance(GenericComponentNode.class, descriptor);

            mockContextInstantiateGraphNode(node);

            JSONObject genericComponentDefinition = new JSONObject(GenericComponent.DynamicTypesWithScript.json());

            // When
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
    }
}
