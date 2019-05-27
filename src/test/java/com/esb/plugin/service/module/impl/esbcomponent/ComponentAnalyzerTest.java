package com.esb.plugin.service.module.impl.esbcomponent;

import com.esb.api.annotation.ESBComponent;
import com.esb.plugin.assertion.PluginAssertion;
import com.esb.plugin.commons.PackageToPath;
import com.esb.plugin.component.domain.ComponentDescriptor;
import com.esb.plugin.component.domain.TypeEnumDescriptor;
import com.esb.plugin.component.domain.TypePrimitiveDescriptor;
import com.esb.plugin.component.scanner.ComponentAnalyzer;
import com.esb.plugin.component.scanner.ComponentAnalyzerContext;
import com.esb.plugin.component.scanner.ComponentIconAndImageProvider;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class ComponentAnalyzerTest {

    private final TypePrimitiveDescriptor INT_TYPE = new TypePrimitiveDescriptor(int.class);
    private final TypePrimitiveDescriptor STRING_TYPE = new TypePrimitiveDescriptor(String.class);
    private final TypeEnumDescriptor TEST_ENUM = new TypeEnumDescriptor(asList("VALUE1", "VALUE2", "VALUE3"), "VALUE1");

    @Mock
    private ComponentIconAndImageProvider imgLoader;

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

        ComponentAnalyzerContext context = Mockito.spy(new ComponentAnalyzerContext(scanResult, imgLoader));
        doReturn(null)
                .when(context)
                .getIconByComponentQualifiedName(anyString());

        doReturn(null)
                .when(context)
                .getImageByComponentQualifiedName(anyString());

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
        PluginAssertion.assertThat(descriptor)
                .isNotHidden()
                .hasDisplayName("Test Component")
                .hasFullyQualifiedName(TestComponent.class.getName())

                // Property 1
                .hasProperty("property1")
                .withDisplayName("Property 1")
                .withDefaultValue(3)
                .required()
                .withType(INT_TYPE)

                // Property 2
                .and()
                .hasProperty("property2")
                .withDisplayName("Property 2")
                .withDefaultValue(null)
                .notRequired()
                .withType(STRING_TYPE)

                // Property 3
                .and()
                .hasProperty("property3")
                .withDisplayName("Enum Property")
                .withDefaultValue("VALUE2")
                .notRequired()
                .withType(TEST_ENUM)

                // Not exposed property
                .and()
                .doesNotHaveProperty("notExposedProperty");
    }

}
