package com.reedelk.plugin.component.type.generic;

import com.google.common.collect.ImmutableMap;
import com.reedelk.module.descriptor.model.ComponentDescriptor;
import com.reedelk.plugin.assertion.PluginAssertion;
import com.reedelk.plugin.fixture.ComponentNode1;
import com.reedelk.plugin.graph.deserializer.AbstractNodeDeserializerTest;
import com.reedelk.plugin.graph.node.GraphNode;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

import static com.reedelk.plugin.component.type.generic.SamplePropertyDescriptors.*;
import static com.reedelk.plugin.component.type.generic.SamplePropertyDescriptors.Primitives.booleanProperty;
import static com.reedelk.plugin.component.type.generic.SamplePropertyDescriptors.Primitives.stringProperty;
import static com.reedelk.plugin.fixture.Json.GenericComponent;
import static java.util.Arrays.asList;

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

        private ComponentDescriptor descriptor = ComponentDescriptor.create()
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
                    .hasDataWithValue("integerObjectProperty", Integer.valueOf("998829743"))
                    .hasDataWithValue("longProperty", 913281L)
                    .hasDataWithValue("longObjectProperty", Long.valueOf("55663"))
                    .hasDataWithValue("floatProperty", 123.234f)
                    .hasDataWithValue("floatObjectProperty",  Float.parseFloat("7843.12"))
                    .hasDataWithValue("doubleProperty", 234.234d)
                    .hasDataWithValue("doubleObjectProperty", Double.parseDouble("11.88877"))
                    .hasDataWithValue("booleanProperty", true)
                    .hasDataWithValue("booleanObjectProperty", Boolean.TRUE)
                    .hasDataWithValue("stringProperty", "my text sample")
                    .hasDataWithValue("bigIntegerProperty", new BigInteger("88923423423"))
                    .hasDataWithValue("bigDecimalProperty", new BigDecimal(1.001))
                    .and().nodesCountIs(2);
        }
    }

    @Nested
    @DisplayName("Component type object properties are de-serialized correctly")
    class TypeObjectsPropertiesDeserialization {

        @Test
        void shouldCorrectlyDeserializeTypeObject() {
            // Given
            ComponentDescriptor descriptor = ComponentDescriptor.create()
                    .propertyDescriptors(asList(stringProperty, TypeObjects.typeObjectProperty))
                    .fullyQualifiedName(ComponentNode1.class.getName())
                    .build();

            GenericComponentNode node = createGraphNodeInstance(GenericComponentNode.class, descriptor);

            mockContextInstantiateGraphNode(node);

            JSONObject genericComponentDefinition = new JSONObject(GenericComponent.WithTypeObject.json());

            // When
            GraphNode lastNode = deserializer.deserialize(root, genericComponentDefinition);

            // Then
            PluginAssertion.assertThat(graph)
                    .node(lastNode).is(node)
                    .hasDataWithValue("stringProperty", "yet another string property")
                    .hasTypeObject("typeObjectProperty")
                    .hasDataWithValue("stringProperty", "sample string property")
                    .hasDataWithValue("integerObjectProperty", Integer.valueOf("255"))
                    .and().and().nodesCountIs(2);
        }

        @Test
        void shouldCorrectlyDeserializeSharedTypeObjectWithReference() {
            // Given
            ComponentDescriptor descriptor = ComponentDescriptor.create()
                    .propertyDescriptors(asList(booleanProperty, TypeObjects.typeObjectSharedProperty))
                    .fullyQualifiedName(ComponentNode1.class.getName())
                    .build();

            GenericComponentNode node = createGraphNodeInstance(GenericComponentNode.class, descriptor);

            mockContextInstantiateGraphNode(node);

            JSONObject genericComponentDefinition = new JSONObject(GenericComponent.WithTypeObjectReference.json());

            // When
            GraphNode lastNode = deserializer.deserialize(root, genericComponentDefinition);

            // Then
            PluginAssertion.assertThat(graph)
                    .node(lastNode).is(node)
                    .hasDataWithValue("booleanProperty", true)
                    .hasTypeObject("typeObjectSharedProperty")
                    .hasDataWithValue("ref", "4ba1b6a0-9644-11e9-bc42-526af7764f64")
                    .and().and().nodesCountIs(2);
        }

        // We expected empty objects still be created in the de-serialized data structure.
        @Test
        void shouldCorrectlyDeserializeSharedEmptyTypeObject() {
            // Given
            ComponentDescriptor descriptor = ComponentDescriptor.create()
                    .propertyDescriptors(asList(booleanProperty, TypeObjects.typeObjectSharedProperty))
                    .fullyQualifiedName(ComponentNode1.class.getName())
                    .build();

            GenericComponentNode node = createGraphNodeInstance(GenericComponentNode.class, descriptor);

            mockContextInstantiateGraphNode(node);

            JSONObject genericComponentDefinition = new JSONObject(GenericComponent.WithTypeObjectEmpty.json());

            // When
            GraphNode lastNode = deserializer.deserialize(root, genericComponentDefinition);

            // Then
            PluginAssertion.assertThat(graph)
                    .node(lastNode).is(node)
                    .hasDataWithValue("booleanProperty", true)
                    .hasTypeObject("typeObjectSharedProperty")
                    .isEmpty()
                    .and().and().nodesCountIs(2);
        }

        // We expect empty objects still be created in the de-serialized data structure.
        @Test
        void shouldCorrectlyDeserializeSharedTypeObjectWhenReferencePropertyNotPresent() {
            // Given
            ComponentDescriptor descriptor = ComponentDescriptor.create()
                    .propertyDescriptors(asList(booleanProperty, TypeObjects.typeObjectSharedProperty))
                    .fullyQualifiedName(ComponentNode1.class.getName())
                    .build();

            GenericComponentNode node = createGraphNodeInstance(GenericComponentNode.class, descriptor);

            mockContextInstantiateGraphNode(node);

            JSONObject genericComponentDefinition = new JSONObject(GenericComponent.WithTypeObjectReferenceNotPresent.json());

            // When
            GraphNode lastNode = deserializer.deserialize(root, genericComponentDefinition);

            // Then
            PluginAssertion.assertThat(graph)
                    .node(lastNode).is(node)
                    .hasDataWithValue("booleanProperty", true)
                    .hasTypeObject("typeObjectSharedProperty")
                    .isEmpty()
                    .and().and().nodesCountIs(2);
        }

        // We expected empty objects still be created in the de-serialized data structure.
        @Test
        void shouldCorrectlyDeserializeMissingSharedTypeObject() {
            // Given
            ComponentDescriptor descriptor = ComponentDescriptor.create()
                    .propertyDescriptors(asList(booleanProperty, TypeObjects.typeObjectSharedProperty))
                    .fullyQualifiedName(ComponentNode1.class.getName())
                    .build();

            GenericComponentNode node = createGraphNodeInstance(GenericComponentNode.class, descriptor);

            mockContextInstantiateGraphNode(node);

            JSONObject genericComponentDefinition = new JSONObject(GenericComponent.WithTypeObjectMissing.json());

            // When
            GraphNode lastNode = deserializer.deserialize(root, genericComponentDefinition);

            // Then
            PluginAssertion.assertThat(graph)
                    .node(lastNode).is(node)
                    .hasDataWithValue("booleanProperty", true)
                    .hasTypeObject("typeObjectSharedProperty")
                    .isEmpty()
                    .and().and().nodesCountIs(2);
        }
    }

    @Nested
    @DisplayName("Component map properties are de-serialized correctly")
    class TypeMapPropertiesDeserialization {

        @Test
        void shouldCorrectlyDeserializeGenericComponentWithMapProperty() {
            // Given
            ComponentDescriptor descriptor = ComponentDescriptor.create()
                    .propertyDescriptors(asList(stringProperty, SpecialTypes.mapProperty))
                    .fullyQualifiedName(ComponentNode1.class.getName())
                    .build();

            GenericComponentNode node = createGraphNodeInstance(GenericComponentNode.class, descriptor);

            mockContextInstantiateGraphNode(node);

            JSONObject genericComponentDefinition = new JSONObject(GenericComponent.WithNotEmptyMapProperty.json());

            // When
            GraphNode lastNode = deserializer.deserialize(root, genericComponentDefinition);

            // Then
            PluginAssertion.assertThat(graph)
                    .node(lastNode).is(node)
                    .hasDataWithValue("stringProperty", "first property")
                    .hasDataWithValue("mapProperty", ImmutableMap.of("key1", "value1", "key2", 3))
                    .and().nodesCountIs(2);
        }
    }

    @Nested
    @DisplayName("Component script properties are de-serialized correctly")
    class TypeScriptPropertiesDeserialization {

        @Test
        void shouldCorrectlyDeserializeGenericComponentWithScriptProperty() {
            // Given
            ComponentDescriptor descriptor = ComponentDescriptor.create()
                    .propertyDescriptors(asList(stringProperty, SpecialTypes.scriptProperty))
                    .fullyQualifiedName(ComponentNode1.class.getName())
                    .build();

            GenericComponentNode node = createGraphNodeInstance(GenericComponentNode.class, descriptor);

            mockContextInstantiateGraphNode(node);

            JSONObject genericComponentDefinition = new JSONObject(GenericComponent.WithScriptProperty.json());

            // When
            GraphNode lastNode = deserializer.deserialize(root, genericComponentDefinition);

            // Then
            PluginAssertion.assertThat(graph)
                    .node(lastNode).is(node)
                    .hasDataWithValue("stringProperty", "string prop")
                    .hasDataWithValue("scriptProperty", "#[message.attributes]")
                    .and().nodesCountIs(2);
        }
    }

    @Nested
    @DisplayName("Component combo properties are de-serialized correctly")
    class TypeComboPropertiesDeserialization {

        @Test
        void shouldCorrectlyDeserializeGenericComponentWithComboProperty() {
            // Given
            ComponentDescriptor descriptor = ComponentDescriptor.create()
                    .propertyDescriptors(asList(Primitives.doubleObjectProperty, SpecialTypes.comboProperty))
                    .fullyQualifiedName(ComponentNode1.class.getName())
                    .build();

            GenericComponentNode node = createGraphNodeInstance(GenericComponentNode.class, descriptor);

            mockContextInstantiateGraphNode(node);

            JSONObject genericComponentDefinition = new JSONObject(GenericComponent.WithComboProperty.json());

            // When
            GraphNode lastNode = deserializer.deserialize(root, genericComponentDefinition);

            // Then
            PluginAssertion.assertThat(graph)
                    .node(lastNode).is(node)
                    .hasDataWithValue("doubleObjectProperty", Double.parseDouble("23491.23432"))
                    .hasDataWithValue("comboProperty", "two")
                    .and().nodesCountIs(2);
        }
    }

    @Nested
    @DisplayName("Component resource properties are de-serialized correctly")
    class TypeResourcePropertiesDeserialization {

        @Test
        void shouldCorrectlyDeserializeGenericComponentWithResourceProperty() {
            // Given
            ComponentDescriptor descriptor = ComponentDescriptor.create()
                    .propertyDescriptors(asList(Primitives.booleanProperty, SpecialTypes.resourceProperty))
                    .fullyQualifiedName(ComponentNode1.class.getName())
                    .build();

            GenericComponentNode node = createGraphNodeInstance(GenericComponentNode.class, descriptor);

            mockContextInstantiateGraphNode(node);

            JSONObject genericComponentDefinition = new JSONObject(GenericComponent.WithResourceProperty.json());

            // When
            GraphNode lastNode = deserializer.deserialize(root, genericComponentDefinition);

            // Then
            PluginAssertion.assertThat(graph)
                    .node(lastNode).is(node)
                    .hasDataWithValue("booleanProperty", true)
                    .hasDataWithValue("resourceProperty", "metadata/schema/person.schema.json")
                    .and().nodesCountIs(2);
        }
    }

    @Nested
    @DisplayName("Component enum properties are de-serialized correctly")
    class TypeEnumPropertiesDeserialization {

        @Test
        void shouldCorrectlyDeserializeGenericComponentWithEnumProperty() {
            // Given
            ComponentDescriptor descriptor = ComponentDescriptor.create()
                    .propertyDescriptors(asList(Primitives.floatProperty, SpecialTypes.enumProperty))
                    .fullyQualifiedName(ComponentNode1.class.getName())
                    .build();

            GenericComponentNode node = createGraphNodeInstance(GenericComponentNode.class, descriptor);

            mockContextInstantiateGraphNode(node);

            JSONObject genericComponentDefinition = new JSONObject(GenericComponent.WithEnumProperty.json());

            // When
            GraphNode lastNode = deserializer.deserialize(root, genericComponentDefinition);

            // Then
            PluginAssertion.assertThat(graph)
                    .node(lastNode).is(node)
                    .hasDataWithValue("floatProperty", 2483.002f)
                    .hasDataWithValue("enumProperty", "CERT")
                    .and().nodesCountIs(2);

        }
    }

    @Nested
    @DisplayName("Component Dynamic properties are de-serialized correctly")
    class TypeDynamicPropertiesDeserialization {

        ComponentDescriptor descriptor = ComponentDescriptor.create()
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
