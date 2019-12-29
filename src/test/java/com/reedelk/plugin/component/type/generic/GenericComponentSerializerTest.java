package com.reedelk.plugin.component.type.generic;

import com.reedelk.plugin.AbstractGraphTest;
import com.reedelk.plugin.commons.TypeObjectFactory;
import com.reedelk.plugin.component.domain.ComponentData;
import com.reedelk.plugin.component.domain.ComponentDescriptor;
import com.reedelk.plugin.fixture.ComponentNode1;
import com.reedelk.plugin.fixture.Json;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.node.GraphNode;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import static com.reedelk.plugin.component.domain.TypeObjectDescriptor.TypeObject;
import static com.reedelk.plugin.component.type.generic.SamplePropertyDescriptors.*;
import static com.reedelk.plugin.component.type.generic.SamplePropertyDescriptors.Primitives.stringProperty;
import static com.reedelk.plugin.graph.serializer.AbstractSerializer.UntilNoSuccessors;
import static java.util.Arrays.asList;

class GenericComponentSerializerTest extends AbstractGraphTest {

    private static final UntilNoSuccessors UNTIL_NO_SUCCESSORS = new UntilNoSuccessors();

    private static GenericComponentSerializer serializer;

    @BeforeAll
    static void beforeAll() {
        serializer = new GenericComponentSerializer();
    }

    @Nested
    @DisplayName("Component primitives types are serialized correctly")
    class PrimitiveTypesSerialization {

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
                        stringProperty,
                        Primitives.bigIntegerProperty,
                        Primitives.bigDecimalProperty))
                .fullyQualifiedName(ComponentNode1.class.getName())
                .build();

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
    }

    @Nested
    @DisplayName("Component type object properties are serialized correctly")
    class TypeObjectsPropertiesSerialization {

        @Test
        void shouldCorrectlySerializeTypeObject() {
            // Given
            ComponentData componentData = new ComponentData(ComponentDescriptor.create()
                    .propertyDescriptors(asList(stringProperty, TypeObjects.typeObjectProperty))
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
                    .propertyDescriptors(asList(Primitives.booleanProperty, TypeObjects.typeObjectSharedProperty))
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
                    .propertyDescriptors(asList(Primitives.booleanProperty, TypeObjects.typeObjectSharedProperty))
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

    @Nested
    @DisplayName("Component map properties are serialized correctly")
    class TypeMapPropertiesSerialization {

        @Test
        void shouldCorrectlySerializeGenericComponentWithNotEmptyMapProperty() {
            // Given
            Map<String, Object> myMap = new HashMap<>();
            myMap.put("key1", "value1");
            myMap.put("key2", 3);

            ComponentData componentData = new ComponentData(ComponentDescriptor.create()
                    .propertyDescriptors(asList(Primitives.stringProperty, SpecialTypes.mapProperty))
                    .fullyQualifiedName(ComponentNode1.class.getName())
                    .build());

            GraphNode componentNode = new GenericComponentNode(componentData);
            componentData.set("stringProperty", "first property");
            componentData.set("mapProperty", myMap);

            // When
            String actualJson = serialize(componentNode);

            // Then
            String expectedJson = Json.GenericComponent.WithNotEmptyMapProperty.json();
            JSONAssert.assertEquals(expectedJson, actualJson, true);
        }


        @Test
        void shouldNotSerializeGenericComponentPropertyWithEmptyMap() {
            // Given
            Map<String, Object> myMap = new HashMap<>();

            ComponentData componentData = new ComponentData(ComponentDescriptor.create()
                    .propertyDescriptors(asList(Primitives.stringProperty, SpecialTypes.mapProperty))
                    .fullyQualifiedName(ComponentNode1.class.getName())
                    .build());

            GraphNode componentNode = new GenericComponentNode(componentData);
            componentData.set("stringProperty", "first property");
            componentData.set("mapProperty", myMap);

            // When
            String actualJson = serialize(componentNode);

            // Then
            String expectedJson = Json.GenericComponent.WithEmptyMapProperty.json();
            JSONAssert.assertEquals(expectedJson, actualJson, true);
        }
    }

    @Nested
    @DisplayName("Component script properties are serialized correctly")
    class TypeScriptPropertiesSerialization {

        @Test
        void shouldCorrectlySerializeGenericComponentWithScriptProperty() {
            // Given
            ComponentData componentData = new ComponentData(ComponentDescriptor.create()
                    .propertyDescriptors(asList(Primitives.stringProperty, SpecialTypes.scriptProperty))
                    .fullyQualifiedName(ComponentNode1.class.getName())
                    .build());

            GraphNode componentNode = new GenericComponentNode(componentData);
            componentData.set("stringProperty", "string prop");
            componentData.set("scriptProperty", "#[message.attributes]");

            // When
            String actualJson = serialize(componentNode);

            // Then
            String expectedJson = Json.GenericComponent.WithScriptProperty.json();
            JSONAssert.assertEquals(expectedJson, actualJson, true);
        }
    }

    @Nested
    @DisplayName("Component combo properties are serialized correctly")
    class TypeComboPropertiesSerialization {

        @Test
        void shouldCorrectlySerializeGenericComponentWithComboProperty() {
            // Given
            ComponentData componentData = new ComponentData(ComponentDescriptor.create()
                    .propertyDescriptors(asList(Primitives.doubleObjectProperty, SpecialTypes.comboProperty))
                    .fullyQualifiedName(ComponentNode1.class.getName())
                    .build());

            GraphNode componentNode = new GenericComponentNode(componentData);
            componentData.set("doubleObjectProperty", Double.parseDouble("23491.23432"));
            componentData.set("comboProperty", "two");

            // When
            String actualJson = serialize(componentNode);

            // Then
            String expectedJson = Json.GenericComponent.WithComboProperty.json();
            JSONAssert.assertEquals(expectedJson, actualJson, true);
        }
    }

    @Nested
    @DisplayName("Component resource properties are serialized correctly")
    class TypeResourcePropertiesSerialization {

        @Test
        void shouldCorrectlySerializeGenericComponentWithResourceProperty() {
            // Given
            ComponentData componentData = new ComponentData(ComponentDescriptor.create()
                    .propertyDescriptors(asList(Primitives.booleanProperty, SpecialTypes.resourceProperty))
                    .fullyQualifiedName(ComponentNode1.class.getName())
                    .build());

            GraphNode componentNode = new GenericComponentNode(componentData);
            componentData.set("booleanProperty", true);
            componentData.set("resourceProperty", "metadata/schema/person.schema.json");

            // When
            String actualJson = serialize(componentNode);

            // Then
            String expectedJson = Json.GenericComponent.WithResourceProperty.json();
            JSONAssert.assertEquals(expectedJson, actualJson, true);
        }
    }

    @Nested
    @DisplayName("Component enum properties are serialized correctly")
    class TypeEnumPropertiesSerialization {

        @Test
        void shouldCorrectlySerializeGenericComponentWithEnumProperty() {
            // Given
            ComponentData componentData = new ComponentData(ComponentDescriptor.create()
                    .propertyDescriptors(asList(Primitives.floatProperty, SpecialTypes.enumProperty))
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

    @Nested
    @DisplayName("Component Dynamic properties are serialized correctly")
    class TypeDynamicPropertiesSerialization {

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
    }

    private String serialize(GraphNode componentNode) {
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, componentNode);

        JSONArray sequence = new JSONArray();
        serializer.serialize(graph, sequence, componentNode, UNTIL_NO_SUCCESSORS);

        JSONObject serializedObject = sequence.getJSONObject(0);
        return serializedObject.toString(2);
    }
}
