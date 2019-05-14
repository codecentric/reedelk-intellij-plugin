package com.esb.plugin.service.module.impl;

import com.esb.api.annotation.ESBComponent;
import com.esb.plugin.component.ComponentDescriptor;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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

    }
}
