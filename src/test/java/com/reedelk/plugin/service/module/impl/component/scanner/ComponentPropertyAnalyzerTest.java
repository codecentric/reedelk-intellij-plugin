package com.reedelk.plugin.service.module.impl.component.scanner;

import com.google.common.collect.ImmutableMap;
import com.reedelk.plugin.assertion.PluginAssertion;
import com.reedelk.plugin.assertion.component.TypeDescriptorMatchers;
import com.reedelk.plugin.component.domain.*;
import com.reedelk.plugin.service.module.impl.component.scanner.property.ComponentPropertyAnalyzer;
import com.reedelk.plugin.testutils.ScannerTestUtils;
import com.reedelk.plugin.testutils.TestComponent;
import com.reedelk.runtime.api.message.content.MimeType;
import com.reedelk.runtime.api.script.dynamicmap.DynamicStringMap;
import com.reedelk.runtime.api.script.dynamicvalue.*;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.FieldInfo;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

import static com.reedelk.plugin.assertion.component.TypeDescriptorMatchers.TypeDescriptorMatcher;
import static org.assertj.core.api.Assertions.assertThat;

class ComponentPropertyAnalyzerTest {

    private static ComponentPropertyAnalyzer analyzer;
    private static ClassInfo componentClassInfo;

    @BeforeAll
    static void beforeAll() {
        ScannerTestUtils.ScanContext scanContext = ScannerTestUtils.scan(TestComponent.class);
        analyzer = new ComponentPropertyAnalyzer(scanContext.context);
        componentClassInfo = scanContext.targetComponentClassInfo;
    }

    @Test
    void shouldCorrectlyAnalyzeIntegerTypeProperty() {
        // Given
        TypePrimitiveDescriptor typeInteger = new TypePrimitiveDescriptor(int.class);

        // Expect
        assertThatExistProperty(
                "integerProperty",
                "Integer property",
                3,
                TypeDescriptorMatchers.ofPrimitiveType(typeInteger));
    }

    @Test
    void shouldCorrectlyAnalyzeIntegerObjectProperty() {
        // Given
        TypePrimitiveDescriptor typeIntegerObject = new TypePrimitiveDescriptor(Integer.class);

        // Expect
        assertThatExistProperty(
                "integerObjectProperty",
                "Integer object property",
                4569,
                TypeDescriptorMatchers.ofPrimitiveType(typeIntegerObject));
    }

    @Test
    void shouldCorrectlyAnalyzeLongTypeProperty() {
        // Given
        TypePrimitiveDescriptor typeLong = new TypePrimitiveDescriptor(long.class);

        // Expect
        assertThatExistProperty(
                "longProperty",
                "Long property",
                0L,
                TypeDescriptorMatchers.ofPrimitiveType(typeLong));
    }

    @Test
    void shouldCorrectlyAnalyzeLongObjectProperty() {
        // Given
        TypePrimitiveDescriptor typeLongObject = new TypePrimitiveDescriptor(Long.class);

        // Expect
        assertThatExistProperty(
                "longObjectProperty",
                "Long object property",
                null,
                TypeDescriptorMatchers.ofPrimitiveType(typeLongObject));
    }

    @Test
    void shouldCorrectlyAnalyzeFloatTypeProperty() {
        // Given
        TypePrimitiveDescriptor typeFloat = new TypePrimitiveDescriptor(float.class);

        // Expect
        assertThatExistProperty(
                "floatProperty",
                "Float property",
                23.23f,
                TypeDescriptorMatchers.ofPrimitiveType(typeFloat));
    }

    @Test
    void shouldCorrectlyAnalyzeFloatObjectTypeProperty() {
        // Given
        TypePrimitiveDescriptor typeFloatObject = new TypePrimitiveDescriptor(Float.class);

        // Expect
        assertThatExistProperty(
                "floatObjectProperty",
                "Float object property",
                13.23f,
                TypeDescriptorMatchers.ofPrimitiveType(typeFloatObject));
    }

    @Test
    void shouldCorrectlyAnalyzeDoubleTypeProperty() {
        // Given
        TypePrimitiveDescriptor typeDouble = new TypePrimitiveDescriptor(double.class);

        // Expect
        assertThatExistProperty(
                "doubleProperty",
                "Double property",
                234.5322343d,
                TypeDescriptorMatchers.ofPrimitiveType(typeDouble));
    }

    @Test
    void shouldCorrectlyAnalyzeDoubleObjectTypeProperty() {
        // Given
        TypePrimitiveDescriptor typeDoubleObject = new TypePrimitiveDescriptor(Double.class);

        // Expect
        assertThatExistProperty(
                "doubleObjectProperty",
                "Double object property",
                234.12d,
                TypeDescriptorMatchers.ofPrimitiveType(typeDoubleObject));
    }

    @Test
    void shouldCorrectlyAnalyzeBooleanTypeProperty() {
        // Given
        TypePrimitiveDescriptor typeBoolean = new TypePrimitiveDescriptor(boolean.class);

        // Expect
        assertThatExistProperty(
                "booleanProperty",
                "Boolean property",
                true,
                TypeDescriptorMatchers.ofPrimitiveType(typeBoolean));
    }

    @Test
    void shouldCorrectlyAnalyzeBooleanObjectTypeProperty() {
        // Given
        TypePrimitiveDescriptor typeBooleanObject = new TypePrimitiveDescriptor(Boolean.class);

        // Expect
        assertThatExistProperty(
                "booleanObjectProperty",
                "Boolean object property",
                true,
                TypeDescriptorMatchers.ofPrimitiveType(typeBooleanObject));
    }

    @Test
    void shouldCorrectlyAnalyzeEnumTypeProperty() {
        // Given
        Map<String, String> enumValues =
                ImmutableMap.of("VALUE1", "Value 1", "VALUE2", "VALUE2", "VALUE3", "Value 3");
        TypeEnumDescriptor typeEnum = new TypeEnumDescriptor(enumValues, "VALUE1");

        // Expect
        assertThatExistProperty(
                "enumProperty",
                "Enum Property",
                "VALUE2",
                TypeDescriptorMatchers.ofTypeEnum(typeEnum));
    }

    @Test
    void shouldCorrectlyAnalyzeStringTypeProperty() {
        // Given
        TypePrimitiveDescriptor typeString = new TypePrimitiveDescriptor(String.class);

        // Expect
        assertThatExistProperty(
                "stringProperty",
                "String property",
                "default string value",
                TypeDescriptorMatchers.ofPrimitiveType(typeString));
    }

    @Test
    void shouldCorrectlyAnalyzeBigIntegerTypeProperty() {
        // Given
        TypePrimitiveDescriptor typeBigInteger = new TypePrimitiveDescriptor(BigInteger.class);

        // Expect
        assertThatExistProperty(
                "bigIntegerProperty",
                "Big Integer property",
                new BigInteger("6723823"),
                TypeDescriptorMatchers.ofPrimitiveType(typeBigInteger));
    }

    @Test
    void shouldCorrectlyAnalyzeBigDecimalTypeProperty() {
        // Given
        TypePrimitiveDescriptor typeBigDecimal = new TypePrimitiveDescriptor(BigDecimal.class);

        // Expect
        assertThatExistProperty(
                "bigDecimalProperty",
                "Big Decimal property",
                new BigDecimal("342.14823"),
                TypeDescriptorMatchers.ofPrimitiveType(typeBigDecimal));
    }

    @Test
    void shouldCorrectlyAnalyzeFileTypeProperty() {
        // Given
        TypeFileDescriptor typeFile = new TypeFileDescriptor();

        // Expect
        assertThatExistProperty(
                "fileProperty",
                "File property",
                null,
                TypeDescriptorMatchers.ofTypeFile(typeFile));
    }

    @Test
    void shouldCorrectlyAnalyzeComboTypeProperty() {
        // Given
        TypeComboDescriptor typeCombo = new TypeComboDescriptor(true, new String[]{"one", "two", "three"});

        // Expect
        assertThatExistProperty(
                "comboProperty",
                "Combo property",
                "two",
                TypeDescriptorMatchers.ofTypeCombo(typeCombo));
    }

    @Test
    void shouldCorrectlyAnalyzeMapTypeProperty() {
        // Given
        TypeMapDescriptor typeMap = new TypeMapDescriptor("Test tab group");

        // Expect
        assertThatExistProperty(
                "mapProperty",
                "Map property",
                null,
                TypeDescriptorMatchers.ofTypeMap(typeMap));
    }

    @Test
    void shouldCorrectlyAnalyzeMapTypePropertyWithDefaultValues() {
        // Given
        TypeMapDescriptor typeMap = new TypeMapDescriptor("Default values tab group");
        Map<String, String> defaults = ImmutableMap.of("key1", "value1", "key2", "value2");

        // Expect
        assertThatExistProperty(
                "mapPropertyWithDefaultValues",
                "Map property with defaults",
                defaults,
                TypeDescriptorMatchers.ofTypeMap(typeMap));

    }

    @Test
    void shouldCorrectlyAnalyzeScriptTypeProperty() {
        // Given
        TypeScriptDescriptor typeScript = new TypeScriptDescriptor();

        // Expect
        assertThatExistProperty(
                "scriptProperty",
                "Script property",
                null,
                TypeDescriptorMatchers.ofTypeScript(typeScript));
    }

    // Dynamic value types

    @Test
    void shouldCorrectlyAnalyzeDynamicBigDecimalProperty() {
        // Given
        TypeDynamicValueDescriptor<DynamicBigDecimal> typeDynamicBigDecimal =
                new TypeDynamicValueDescriptor<>(DynamicBigDecimal.class);

        // Expect
        assertThatExistProperty(
                "dynamicBigDecimalProperty",
                "Dynamic big decimal",
                "#[56789.234]",
                TypeDescriptorMatchers.ofDynamicType(typeDynamicBigDecimal));
    }

    @Test
    void shouldCorrectlyAnalyzeDynamicBigIntegerProperty() {
        // Given
        TypeDynamicValueDescriptor<DynamicBigInteger> typeDynamicBigInteger =
                new TypeDynamicValueDescriptor<>(DynamicBigInteger.class);

        // Expect
        assertThatExistProperty(
                "dynamicBigIntegerProperty",
                "Dynamic big integer",
                new BigInteger("56789"),
                TypeDescriptorMatchers.ofDynamicType(typeDynamicBigInteger));
    }

    @Test
    void shouldCorrectlyAnalyzeDynamicBooleanProperty() {
        // Given
        TypeDynamicValueDescriptor<DynamicBoolean> typeDynamicBoolean =
                new TypeDynamicValueDescriptor<>(DynamicBoolean.class);

        // Expect
        assertThatExistProperty(
                "dynamicBooleanProperty",
                "Dynamic boolean",
                true,
                TypeDescriptorMatchers.ofDynamicType(typeDynamicBoolean));
    }

    @Test
    void shouldCorrectlyAnalyzeDynamicByteArrayProperty() {
        // Given
        TypeDynamicValueDescriptor<DynamicByteArray> typeDynamicByteArray =
                new TypeDynamicValueDescriptor<>(DynamicByteArray.class);

        // Expect
        assertThatExistProperty(
                "dynamicByteArrayProperty",
                "Dynamic byte array",
                "#[message.payload()]",
                TypeDescriptorMatchers.ofDynamicType(typeDynamicByteArray));
    }

    @Test
    void shouldCorrectlyAnalyzeDynamicDoubleProperty() {
        // Given
        TypeDynamicValueDescriptor<DynamicDouble> typeDynamicDouble =
                new TypeDynamicValueDescriptor<>(DynamicDouble.class);

        // Expect
        assertThatExistProperty(
                "dynamicDoubleProperty",
                "Dynamic double",
                Double.parseDouble("234.23d"),
                TypeDescriptorMatchers.ofDynamicType(typeDynamicDouble));
    }

    @Test
    void shouldCorrectlyAnalyzeDynamicFloatProperty() {
        // Given
        TypeDynamicValueDescriptor<DynamicFloat> typeDynamicFloat =
                new TypeDynamicValueDescriptor<>(DynamicFloat.class);

        // Expect
        assertThatExistProperty(
                "dynamicFloatProperty",
                "Dynamic float",
                "#[message.attributes['id']]",
                TypeDescriptorMatchers.ofDynamicType(typeDynamicFloat));
    }

    @Test
    void shouldCorrectlyAnalyzeDynamicIntegerProperty() {
        // Given
        TypeDynamicValueDescriptor<DynamicInteger> typeDynamicInteger =
                new TypeDynamicValueDescriptor<>(DynamicInteger.class);

        // Expect
        assertThatExistProperty(
                "dynamicIntegerProperty",
                "Dynamic integer",
                1233,
                TypeDescriptorMatchers.ofDynamicType(typeDynamicInteger));
    }

    @Test
    void shouldCorrectlyAnalyzeDynamicLongProperty() {
        // Given
        TypeDynamicValueDescriptor<DynamicLong> typeDynamicLong =
                new TypeDynamicValueDescriptor<>(DynamicLong.class);

        // Expect
        assertThatExistProperty(
                "dynamicLongProperty",
                "Dynamic long",
                658291L,
                TypeDescriptorMatchers.ofDynamicType(typeDynamicLong));
    }

    @Test
    void shouldCorrectlyAnalyzeDynamicObjectProperty() {
        // Given
        TypeDynamicValueDescriptor<DynamicObject> typeDynamicObject =
                new TypeDynamicValueDescriptor<>(DynamicObject.class);

        // Expect
        assertThatExistProperty(
                "dynamicObjectProperty",
                "Dynamic object",
                "my object text",
                TypeDescriptorMatchers.ofDynamicType(typeDynamicObject));
    }

    @Test
    void shouldCorrectlyAnalyzeDynamicStringProperty() {
        // Given
        TypeDynamicValueDescriptor<DynamicString> typeDynamicString =
                new TypeDynamicValueDescriptor<>(DynamicString.class);

        // Expect
        assertThatExistProperty(
                "dynamicStringProperty",
                "Dynamic string",
                null,
                TypeDescriptorMatchers.ofDynamicType(typeDynamicString));
    }

    // Dynamic map types

    @Test
    void shouldCorrectlyAnalyzeDynamicStringMapProperty() {
        // Given
        TypeDynamicMapDescriptor<DynamicStringMap> typeDynamicMapDescriptor =
                new TypeDynamicMapDescriptor<>(DynamicStringMap.class, "My dynamic string map");

        // Expect
        assertThatExistProperty(
                "dynamicStringMapProperty",
                "Dynamic string map",
                null,
                TypeDescriptorMatchers.ofDynamicMapType(typeDynamicMapDescriptor));
    }

    // Mime Type Combo

    @Test
    void shouldCorrectlyAnalyzeMimeTypeComboProperty() {
        // Given
        TypeComboDescriptor typeComboDescriptor =
                new TypeComboDescriptor(true, MimeType.ALL_MIME_TYPES, MimeType.MIME_TYPE_PROTOTYPE);

        // Expect
        assertThatExistProperty(
                "mimeType",
                "Mime type",
                "*/*",
                TypeDescriptorMatchers.ofTypeCombo(typeComboDescriptor));
    }

    @Test
    void shouldCorrectlyAnalyzeMimeTypeCustomComboProperty() {
        // Given
        List<String> predefinedMimeTypes = new ArrayList<>(Arrays.asList(MimeType.ALL_MIME_TYPES));
        predefinedMimeTypes.add("img/xyz");
        predefinedMimeTypes.add("audio/mp13");
        String[] comboMimeTypesArray = predefinedMimeTypes.toArray(new String[]{});

        // Given
        TypeComboDescriptor typeComboDescriptor =
                new TypeComboDescriptor(true, comboMimeTypesArray, MimeType.MIME_TYPE_PROTOTYPE);

        // Expect
        assertThatExistProperty(
                "mimeTypeCustom",
                "Mime type with additional types",
                "img/xyz",
                TypeDescriptorMatchers.ofTypeCombo(typeComboDescriptor));
    }

    // Special cases

    @Test
    void shouldCorrectlyReturnEmptyOptionalForNotExposedProperty() {
        // Given
        FieldInfo notExposedProperty =
                componentClassInfo.getFieldInfo("notExposedProperty");

        // When
        Optional<ComponentPropertyDescriptor> propertyDescriptor = analyzer.analyze(notExposedProperty);

        // Then
        assertThat(propertyDescriptor).isNotPresent();
    }

    @Test
    void shouldReturnFieldNameWhenPropertyAnnotationDoesNotContainPropertyDisplayName() {
        // Given
        TypePrimitiveDescriptor typeFloat = new TypePrimitiveDescriptor(float.class);

        // Expect
        assertThatExistProperty(
                "withoutDisplayNameProperty",
                "withoutDisplayNameProperty",
                0.0f,
                TypeDescriptorMatchers.ofPrimitiveType(typeFloat));
    }

    @Test
    void shouldReturnDefaultIntValueWhenDefaultAnnotationDoesNotSpecifyAnyValue() {
        // Given
        TypePrimitiveDescriptor typeInteger = new TypePrimitiveDescriptor(int.class);

        // Expect
        assertThatExistProperty(
                "missingDefaultValueProperty",
                "Property with missing default value",
                0,
                TypeDescriptorMatchers.ofPrimitiveType(typeInteger));
    }

    private void assertThatExistProperty(String propertyName, String displayName, Object defaultValue, TypeDescriptorMatcher matcher) {
        // Given
        FieldInfo property = componentClassInfo.getFieldInfo(propertyName);

        // When
        Optional<ComponentPropertyDescriptor> descriptor = analyzer.analyze(property);

        // Then
        assertThat(descriptor).isPresent();
        PluginAssertion.assertThat(descriptor.get())
                .hasName(propertyName)
                .hasDisplayName(displayName)
                .hasDefaultValue(defaultValue)
                .hasType(matcher);
    }
}
