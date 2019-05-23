package com.esb.plugin.service.module.impl.esbcomponent;

import com.esb.api.annotation.ESBComponent;
import com.esb.plugin.commons.PackageToPath;
import com.esb.plugin.component.ComponentDescriptor;
import com.esb.plugin.component.ComponentPropertyDescriptor;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

class ComponentAnalyzerTest {

    private ScanResult scanResult;
    private ComponentAnalyzer analyzer;

    @BeforeEach
    void setUp() {
        String whiteListPaths = PackageToPath.convert(ComponentAnalyzerTest.class.getPackage().getName());
        scanResult = new ClassGraph()
                .whitelistPaths(whiteListPaths)
                .enableFieldInfo()
                .enableAnnotationInfo()
                .ignoreFieldVisibility()
                .scan();

        ComponentIconsAnalyzer iconsAnalyzer = new ComponentIconsAnalyzer(scanResult);

        ComponentAnalyzerContext context = new ComponentAnalyzerContext(scanResult, iconsAnalyzer);
        analyzer = new ComponentAnalyzer(context);
    }

    @Test
    void shouldCorrectlyAnalyzeClassInfo() {
        // Given
        ClassInfoList classesWithAnnotation = scanResult.getClassesWithAnnotation(ESBComponent.class.getName());
        ClassInfo testComponentClassInfo = classesWithAnnotation.get(0);

        // When
        ComponentDescriptor descriptor = analyzer.analyze(testComponentClassInfo);

        // Then
        String displayName = descriptor.getDisplayName();
        assertThat(displayName).isEqualTo("Test Component");

        assertPropertiesNamesAreExactlyInAnyOrder(descriptor, "property1", "property2", "property3");
        assertExistsPropertyDefinition(descriptor, "property1", "Property 1", 3, int.class, true);
        assertExistsPropertyDefinition(descriptor, "property2", "Property 2", null, String.class, false);
    }

    private void assertPropertiesNamesAreExactlyInAnyOrder(ComponentDescriptor descriptor, String... expectedPropertyNames) {
        List<String> actualPropertyNames = descriptor
                .getPropertiesDescriptors()
                .stream()
                .map(ComponentPropertyDescriptor::getPropertyName)
                .collect(toList());
        assertThat(actualPropertyNames).containsExactlyInAnyOrder(expectedPropertyNames);
    }

    private void assertExistsPropertyDefinition(ComponentDescriptor descriptor,
                                                String expectedPropertyName,
                                                String expectedDisplayName,
                                                Object expectedDefaultValue,
                                                Class<?> expectedPropertyType,
                                                boolean expectedIsRequired) {
        Optional<ComponentPropertyDescriptor> propertyDescriptor = descriptor.getPropertyDescriptor(expectedPropertyName);
        assertThat(propertyDescriptor).isPresent();
        ComponentPropertyDescriptor definition = propertyDescriptor.get();

        assertThat(definition.getDisplayName()).isEqualTo(expectedDisplayName);
        assertThat(definition.getPropertyName()).isEqualTo(expectedPropertyName);
        assertThat(definition.getDefaultValue()).isEqualTo(expectedDefaultValue);
        assertThat(definition.getPropertyType().type()).isEqualTo(expectedPropertyType);
        assertThat(definition.isRequired()).isEqualTo(expectedIsRequired);
    }
}
