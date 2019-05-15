package com.esb.plugin.service.module.impl;

import com.esb.api.annotation.ESBComponent;
import com.esb.plugin.component.ComponentDescriptor;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class ComponentAnalyzerTest {

    private ComponentAnalyzer analyzer = new ComponentAnalyzer();

    @BeforeEach
    void setUp() {
        analyzer = new ComponentAnalyzer();
    }

    @Test
    void shouldCorrectlyAnalyzeClassInfo() {
        // Given
        ScanResult scanResult = new ClassGraph()
                .whitelistClasses(TestComponent.class.getName())
                .enableSystemJarsAndModules()
                .enableAllInfo()
                .scan();

        ClassInfoList classesWithAnnotation = scanResult.getClassesWithAnnotation(ESBComponent.class.getName());
        ClassInfo testComponentClassInfo = classesWithAnnotation.get(0);

        // When
        ComponentDescriptor descriptor = analyzer.analyze(testComponentClassInfo);

        // Then
        String displayName = descriptor.getDisplayName();
        assertThat(displayName).isEqualTo("Test Component");
        assertThat(descriptor.getPropertiesNames()).containsExactlyInAnyOrder("property1", "property2");

        assertExistsPropertyDefinition(descriptor, "property1", "Property 1", 3, int.class, true);

        // TODO: Check default value for String. Should it be empty or null !?
        assertExistsPropertyDefinition(descriptor, "property2", "Property 2", null, String.class, false);
    }

    @Test
    void shouldCorrectlyAnalyzeEnum() {
        // Given
        ScanResult scanResult = new ClassGraph()
                .whitelistPackages("com.esb.plugin.service.module.impl")
                .enableSystemJarsAndModules()
                .enableAllInfo()
                .scan();

        // When
        ClassInfoList classesWithAnnotation = scanResult.getClassesWithAnnotation(ESBComponent.class.getName());
        ClassInfo testComponentClassInfo = classesWithAnnotation.get(0);


        // Then
        System.out.println("te");
    }

    private void assertExistsPropertyDefinition(ComponentDescriptor descriptor,
                                                String expectedPropertyName,
                                                String expectedDisplayName,
                                                Object expectedDefaultValue,
                                                Class<?> expectedPropertyType,
                                                boolean expectedIsRequired) {


        Optional<PropertyDefinition> property1Definition = descriptor.getPropertyDefinition(expectedPropertyName);
        assertThat(property1Definition).isPresent();

        PropertyDefinition definition = property1Definition.get();
        assertThat(definition.getDisplayName()).isEqualTo(expectedDisplayName);
        assertThat(definition.getPropertyName()).isEqualTo(expectedPropertyName);
        assertThat(definition.getDefaultValue()).isEqualTo(expectedDefaultValue);
        assertThat(definition.getPropertyType()).isEqualTo(expectedPropertyType);
        assertThat(definition.isRequired()).isEqualTo(expectedIsRequired);


    }
}
