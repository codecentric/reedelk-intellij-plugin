package com.esb.plugin.component.scanner;

import com.esb.api.annotation.ESBComponent;
import com.esb.plugin.assertion.PluginAssertion;
import com.esb.plugin.component.domain.ComponentPropertyDescriptor;
import com.esb.plugin.component.domain.TypeEnumDescriptor;
import com.esb.plugin.component.domain.TypePrimitiveDescriptor;
import io.github.classgraph.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

class ComponentPropertyAnalyzerTest {

    private final TypePrimitiveDescriptor INT_TYPE = new TypePrimitiveDescriptor(int.class);
    private final TypePrimitiveDescriptor FLOAT_TYPE = new TypePrimitiveDescriptor(float.class);
    private final TypePrimitiveDescriptor STRING_TYPE = new TypePrimitiveDescriptor(String.class);
    private final TypeEnumDescriptor TEST_ENUM = new TypeEnumDescriptor(asList("VALUE1", "VALUE2", "VALUE3"), "VALUE1");

    private ComponentPropertyAnalyzer analyzer;
    private ClassInfo testComponentClassInfo;

    @BeforeEach
    void setUp() {
        ScanResult scanResult = new ClassGraph()
                .whitelistPackages(ComponentPropertyAnalyzerTest.class.getPackage().getName())
                .enableFieldInfo()
                .enableAnnotationInfo()
                .ignoreFieldVisibility()
                .scan();

        ComponentAnalyzerContext context = new ComponentAnalyzerContext(scanResult);
        analyzer = new ComponentPropertyAnalyzer(context);

        ClassInfoList classesWithAnnotation = scanResult.getClassesWithAnnotation(ESBComponent.class.getName());
        testComponentClassInfo = classesWithAnnotation.get(0);
    }

    @Test
    void shouldCorrectlyAnalyzeIntTypeProperty() {
        // Given
        FieldInfo property1 =
                testComponentClassInfo.getFieldInfo("property1");

        // When
        Optional<ComponentPropertyDescriptor> propertyDescriptor = analyzer.analyze(property1);

        // Then
        PluginAssertion.assertThat(propertyDescriptor.get())
                .hasName("property1")
                .hasDisplayName("Property 1")
                .hasDefaultValue(3)
                .required()
                .hasType(INT_TYPE);
    }

    @Test
    void shouldCorrectlyAnalyzeStringTypeProperty() {
        // Given
        FieldInfo property2 =
                testComponentClassInfo.getFieldInfo("property2");

        // When
        Optional<ComponentPropertyDescriptor> propertyDescriptor = analyzer.analyze(property2);

        // Then
        PluginAssertion.assertThat(propertyDescriptor.get())
                .hasName("property2")
                .hasDisplayName("Property 2")
                .hasDefaultValue(null)
                .notRequired()
                .hasType(STRING_TYPE);
    }

    @Test
    void shouldCorrectlyAnalyzeEnumTypeProperty() {
        // Given
        FieldInfo property3 =
                testComponentClassInfo.getFieldInfo("property3");

        // When
        Optional<ComponentPropertyDescriptor> propertyDescriptor = analyzer.analyze(property3);

        // Then
        PluginAssertion.assertThat(propertyDescriptor.get())
                .hasName("property3")
                .hasDisplayName("Enum Property")
                .hasDefaultValue("VALUE2")
                .notRequired()
                .hasType(TEST_ENUM);
    }

    @Test
    void shouldCorrectlyReturnEmptyOptionalForNotExposedProperty() {
        // Given
        FieldInfo notExposedProperty =
                testComponentClassInfo.getFieldInfo("notExposedProperty");

        // When
        Optional<ComponentPropertyDescriptor> propertyDescriptor = analyzer.analyze(notExposedProperty);

        // Then
        assertThat(propertyDescriptor).isNotPresent();
    }

    @Test
    void shouldReturnFieldNameWhenPropertyAnnotationDoesNotContainPropertyDisplayName() {
        // Given
        FieldInfo propertyWithoutDisplayName =
                testComponentClassInfo.getFieldInfo("propertyWithoutDisplayName");

        // When
        Optional<ComponentPropertyDescriptor> propertyDescriptor = analyzer.analyze(propertyWithoutDisplayName);

        // Then
        PluginAssertion.assertThat(propertyDescriptor.get())
                .hasName("propertyWithoutDisplayName")
                .hasDisplayName("propertyWithoutDisplayName")
                .hasDefaultValue(0.0f)
                .notRequired()
                .hasType(FLOAT_TYPE);
    }
}
