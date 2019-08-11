package com.reedelk.plugin.component.scanner;

import com.reedelk.plugin.assertion.PluginAssertion;
import com.reedelk.plugin.component.domain.ComponentClass;
import com.reedelk.plugin.component.domain.ComponentDescriptor;
import com.reedelk.plugin.component.domain.ComponentPropertyDescriptor;
import com.reedelk.plugin.component.scanner.property.ComponentPropertyAnalyzer;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.FieldInfo;
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
class ComponentAnalyzerTest extends AbstractScannerTest {

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

    private ComponentAnalyzer analyzer;

    @BeforeEach
    void setUp() {
        super.setUp();
        ComponentAnalyzerContext context = spy(context());

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
        ClassInfo testComponentClassInfo = getTargetComponentClassInfo();

        // When
        ComponentDescriptor descriptor = analyzer.analyze(testComponentClassInfo);

        // Then
        PluginAssertion.assertThat(descriptor)
                .isNotHidden()
                .propertyCount(3)
                .hasIcon(mockIcon)
                .hasImage(mockImage)
                .hasDisplayName("Test Component")
                .hasClass(ComponentClass.PROCESSOR)
                .hasFullyQualifiedName(TestComponent.class.getName());
    }

    @Override
    protected Class targetComponentClazz() {
        return TestComponent.class;
    }
}
