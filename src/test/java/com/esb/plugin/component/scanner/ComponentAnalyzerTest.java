package com.esb.plugin.component.scanner;

import com.esb.api.annotation.ESBComponent;
import com.esb.plugin.assertion.PluginAssertion;
import com.esb.plugin.commons.PackageToPath;
import com.esb.plugin.component.domain.ComponentDescriptor;
import com.esb.plugin.component.domain.ComponentPropertyDescriptor;
import io.github.classgraph.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.swing.*;
import java.awt.*;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ComponentAnalyzerTest {

    @Mock
    private ComponentPropertyAnalyzer propertyAnalyzer;
    @Mock
    private Image mockImage;
    @Mock
    private Icon mockIcon;
    @Mock
    private ComponentPropertyDescriptor descriptor1;
    @Mock
    private ComponentPropertyDescriptor descriptor2;
    @Mock
    private ComponentPropertyDescriptor descriptor3;


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

        ComponentAnalyzerContext context = spy(new ComponentAnalyzerContext(scanResult));

        doReturn(mockIcon)
                .when(context)
                .getIconByComponentQualifiedName(anyString());

        doReturn(mockImage)
                .when(context)
                .getImageByComponentQualifiedName(anyString());

        doReturn(of(descriptor1), of(descriptor2), of(descriptor3), empty())
                .when(propertyAnalyzer).analyze(any(FieldInfo.class));

        analyzer = new ComponentAnalyzer(context, propertyAnalyzer);
    }

    @Test
    void shouldCorrectlyAnalyzeClassInfo() {
        // Given
        ClassInfoList classesWithAnnotation = scanResult.getClassesWithAnnotation(ESBComponent.class.getName());
        ClassInfo testComponentClassInfo = classesWithAnnotation.get(0);

        // When
        ComponentDescriptor descriptor = analyzer.analyze(testComponentClassInfo);

        // Then
        PluginAssertion.assertThat(descriptor)
                .isNotHidden()
                .propertyCount(3)
                .hasIcon(mockIcon)
                .hasImage(mockImage)
                .hasDisplayName("Test Component")
                .hasFullyQualifiedName(TestComponent.class.getName());
    }

}
