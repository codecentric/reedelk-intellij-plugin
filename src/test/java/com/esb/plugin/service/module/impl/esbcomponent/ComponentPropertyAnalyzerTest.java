package com.esb.plugin.service.module.impl.esbcomponent;

import com.esb.plugin.component.domain.ComponentPropertyDescriptor;
import com.esb.plugin.component.domain.EnumTypeDescriptor;
import com.esb.plugin.component.domain.TypeDescriptor;
import com.esb.plugin.component.scanner.ComponentAnalyzerContext;
import com.esb.plugin.component.scanner.ComponentIconAndImageLoader;
import com.esb.plugin.component.scanner.ComponentPropertyAnalyzer;
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

        ComponentIconAndImageLoader iconsAnalyzer = new ComponentIconAndImageLoader(scanResult);

        ComponentAnalyzerContext context = new ComponentAnalyzerContext(scanResult, iconsAnalyzer);
        analyzer = new ComponentPropertyAnalyzer(context);
    }

    @Test
    void shouldReturnEmptyDescriptorWhenPropertyAnnotationIsNotPresent() {
        // Given
        ClassInfo componentInfo = scanResult.getClassInfo(TestComponent.class.getName());
        FieldInfo notExposedPropertyInfo = componentInfo.getFieldInfo("notExposedProperty");

        // When
        Optional<ComponentPropertyDescriptor> descriptor = analyzer.analyze(notExposedPropertyInfo);

        // Then
        assertThat(descriptor).isEmpty();
    }

    @Test
    void shouldCorrectlyReturnPropertyDescriptorForEnumType() {
        // Given
        ClassInfo componentInfo = scanResult.getClassInfo(TestComponent.class.getName());
        FieldInfo enumTypeProperty = componentInfo.getFieldInfo("property3");

        // When
        Optional<ComponentPropertyDescriptor> descriptor = analyzer.analyze(enumTypeProperty);

        // Then
        assertThat(descriptor).isPresent();

        ComponentPropertyDescriptor enumDescriptor = descriptor.get();
        assertThat(enumDescriptor.getPropertyName()).isEqualTo("property3");
        assertThat(enumDescriptor.getDisplayName()).isEqualTo("Enum Property");
        assertThat(enumDescriptor.isRequired()).isFalse();

        TypeDescriptor propertyType = enumDescriptor.getPropertyType();
        assertThat(propertyType).isInstanceOf(EnumTypeDescriptor.class);

        EnumTypeDescriptor enumTypeDescriptor = (EnumTypeDescriptor) propertyType;
        assertThat(enumTypeDescriptor.possibleValues()).containsExactly("VALUE1", "VALUE2", "VALUE3");
        assertThat(enumTypeDescriptor.defaultValue()).isEqualTo("VALUE1");
    }
}
