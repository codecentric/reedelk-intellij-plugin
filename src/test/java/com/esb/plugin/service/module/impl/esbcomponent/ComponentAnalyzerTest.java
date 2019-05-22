package com.esb.plugin.service.module.impl.esbcomponent;

import com.esb.api.annotation.ESBComponent;
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
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class ComponentAnalyzerTest {

    private ScanResult scanResult;
    private ComponentAnalyzer analyzer;

    @BeforeEach
    void setUp() {
        scanResult = new ClassGraph()
                .whitelistPackages(ComponentAnalyzerTest.class.getPackage().getName())
                .whitelistPaths("/com/esb/system/component")
                .enableAllInfo()
                .scan();

        ComponentAnalyzerContext context = new ComponentAnalyzerContext(scanResult);
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

        List<String> propertyNames = descriptor
                .getPropertiesDescriptors()
                .stream()
                .map(ComponentPropertyDescriptor::getPropertyName)
                .collect(Collectors.toList());
        assertThat(propertyNames).containsExactlyInAnyOrder("property1", "property2", "property3");

        assertExistsPropertyDefinition(descriptor, "property1", "Property 1", 3, int.class, true);
        assertExistsPropertyDefinition(descriptor, "property2", "Property 2", null, String.class, false);
    }

    private void assertExistsPropertyDefinition(ComponentDescriptor descriptor,
                                                String expectedPropertyName,
                                                String expectedDisplayName,
                                                Object expectedDefaultValue,
                                                Class<?> expectedPropertyType,
                                                boolean expectedIsRequired) {
        Optional<ComponentPropertyDescriptor> property1Definition = descriptor.getPropertyDescriptor(expectedPropertyName);
        assertThat(property1Definition).isPresent();

        ComponentPropertyDescriptor definition = property1Definition.get();
        assertThat(definition.getDisplayName()).isEqualTo(expectedDisplayName);
        assertThat(definition.getPropertyName()).isEqualTo(expectedPropertyName);
        assertThat(definition.getDefaultValue()).isEqualTo(expectedDefaultValue);
        assertThat(definition.getPropertyType().type()).isEqualTo(expectedPropertyType);
        assertThat(definition.isRequired()).isEqualTo(expectedIsRequired);
    }
}
