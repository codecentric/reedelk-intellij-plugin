package com.esb.plugin.component.scanner;

import com.esb.plugin.component.domain.TypeEnumDescriptor;
import com.esb.plugin.component.domain.TypePrimitiveDescriptor;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static java.util.Arrays.asList;

class ComponentPropertyAnalyzerTest {

    private final TypePrimitiveDescriptor INT_TYPE = new TypePrimitiveDescriptor(int.class);
    private final TypePrimitiveDescriptor STRING_TYPE = new TypePrimitiveDescriptor(String.class);
    private final TypeEnumDescriptor TEST_ENUM = new TypeEnumDescriptor(asList("VALUE1", "VALUE2", "VALUE3"), "VALUE1");

    private ComponentPropertyAnalyzer analyzer;
    private ScanResult scanResult;

    @BeforeEach
    void setUp() {
        ScanResult scanResult = new ClassGraph()
                .whitelistPackages(ComponentPropertyAnalyzerTest.class.getPackage().getName())
                .enableSystemJarsAndModules()
                .enableAllInfo()
                .scan();

        ComponentAnalyzerContext context = new ComponentAnalyzerContext(scanResult);
        analyzer = new ComponentPropertyAnalyzer(context);
    }

    @Test
    void shouldAnalyzeProperties() {
        /**
        // Given
         ClassInfoList classesWithAnnotation = scanResult.getClassesWithAnnotation(ESBComponent.class.getName());
         ClassInfo testComponentClassInfo = classesWithAnnotation.get(0);


        // When
         ComponentDescriptor descriptor = analyzer.analyze(testComponentClassInfo);


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
         */
    }

}
