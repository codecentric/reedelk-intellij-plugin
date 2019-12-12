package com.reedelk.plugin.service.module.impl.component.scanner.property;

import com.reedelk.plugin.assertion.PluginAssertion;
import com.reedelk.plugin.assertion.component.ScriptSignatureDefinitionMatchers;
import com.reedelk.plugin.component.domain.ComponentPropertyDescriptor;
import com.reedelk.plugin.component.domain.TypeScriptDescriptor;
import com.reedelk.plugin.service.module.impl.component.scanner.ComponentAnalyzerContext;
import com.reedelk.plugin.testutils.ScannerTestUtils;
import com.reedelk.plugin.testutils.TestComponentWithScriptSignature;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.FieldInfo;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;

@ExtendWith(MockitoExtension.class)
class ScriptSignatureFieldInfoAnalyzerTest {

    @Mock
    private ComponentAnalyzerContext context;

    private ScriptSignatureFieldInfoAnalyzer analyzer = new ScriptSignatureFieldInfoAnalyzer();

    private static ClassInfo componentClassInfo;

    @BeforeAll
    static void beforeAll() {
        ScannerTestUtils.ScanContext scanContext = ScannerTestUtils.scan(TestComponentWithScriptSignature.class);
        componentClassInfo = scanContext.targetComponentClassInfo;
    }

    @Test
    void shouldCorrectlyCreateScriptSignatureDefinition() {
        // Given
        String propertyName = "scriptPropertyWithScriptSignature";
        FieldInfo property = componentClassInfo.getFieldInfo(propertyName);

        ComponentPropertyDescriptor.Builder builder =
                ComponentPropertyDescriptor.builder()
                        .propertyName(propertyName)
                        .type(new TypeScriptDescriptor());

        // When
        analyzer.handle(property, builder, context);

        // Then
        PluginAssertion.assertThat(builder.build())
                .hasScriptSignatureDefinition(
                        ScriptSignatureDefinitionMatchers.with(Arrays.asList("arg1","arg2","arg3")));
    }

    @Test
    void shouldReturnEmptyScriptSignatureDefinition() {
        // Given
        String propertyName = "scriptPropertyWithoutScriptSignature";
        FieldInfo property = componentClassInfo.getFieldInfo(propertyName);

        ComponentPropertyDescriptor.Builder builder =
                ComponentPropertyDescriptor.builder()
                        .propertyName(propertyName)
                        .type(new TypeScriptDescriptor());

        // When
        analyzer.handle(property, builder, context);

        // Then
        PluginAssertion.assertThat(builder.build()).hasNotScriptSignatureDefinition();
    }
}