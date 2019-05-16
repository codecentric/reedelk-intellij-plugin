package com.esb.plugin.service.module.impl;

import com.esb.plugin.component.ComponentPropertyDescriptor;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.FieldInfo;
import io.github.classgraph.ScanResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class ComponentPropertyAnalyzerTest {

    private ComponentPropertyAnalyzer analyzer;
    private ScanResult scanResult;

    @BeforeEach
    void setUp() {
        scanResult = new ClassGraph()
                .whitelistPackages(ComponentPropertyAnalyzerTest.class.getPackage().getName())
                .enableSystemJarsAndModules()
                .enableAllInfo()
                .scan();

        ComponentAnalyzerContext context = new ComponentAnalyzerContext(scanResult);
        analyzer = new ComponentPropertyAnalyzer(context);
    }

    @Test
    void shouldReturnEmptyDescriptorWhenPropertyAnnotationIsNotPresent() {
        // Given
        ClassInfo testComponentInfo = scanResult.getClassInfo(TestComponent.class.getName());
        FieldInfo notExposedPropertyInfo = testComponentInfo.getFieldInfo("notExposedProperty");

        // When
        Optional<ComponentPropertyDescriptor> optionalDescriptor = analyzer.analyze(notExposedPropertyInfo);

        // Then
        assertThat(optionalDescriptor).isEmpty();
    }
}
