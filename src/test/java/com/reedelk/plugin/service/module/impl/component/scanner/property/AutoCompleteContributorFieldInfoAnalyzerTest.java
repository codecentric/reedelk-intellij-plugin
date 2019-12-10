package com.reedelk.plugin.service.module.impl.component.scanner.property;

import com.reedelk.plugin.assertion.PluginAssertion;
import com.reedelk.plugin.assertion.component.AutoCompleteContributorDefinitionMatchers;
import com.reedelk.plugin.component.domain.ComponentPropertyDescriptor;
import com.reedelk.plugin.component.domain.TypeDynamicValueDescriptor;
import com.reedelk.plugin.service.module.impl.component.scanner.AbstractScannerTest;
import com.reedelk.plugin.service.module.impl.component.scanner.ComponentAnalyzerContext;
import com.reedelk.plugin.testutils.TestComponentWithAutoCompleteContributor;
import com.reedelk.runtime.api.script.dynamicvalue.DynamicString;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.FieldInfo;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;

@ExtendWith(MockitoExtension.class)
class AutoCompleteContributorFieldInfoAnalyzerTest extends AbstractScannerTest {

    @Mock
    private ComponentAnalyzerContext context;

    private AutoCompleteContributorFieldInfoAnalyzer analyzer = new AutoCompleteContributorFieldInfoAnalyzer();

    private static ClassInfo componentClassInfo;

    @BeforeAll
    static void beforeAll() {
        ScanContext scanContext = scan(TestComponentWithAutoCompleteContributor.class);
        componentClassInfo = scanContext.targetComponentClassInfo;
    }

    @Test
    void shouldCorrectlyCreateAutoCompleteContributorDefinition() {
        // Given
        String propertyName = "propertyWithAutoCompleteContributor";
        FieldInfo propertyWithAutoCompleteContributor =
                componentClassInfo.getFieldInfo(propertyName);

        ComponentPropertyDescriptor.Builder builder =
                ComponentPropertyDescriptor.builder()
                        .propertyName(propertyName)
                        .type(new TypeDynamicValueDescriptor<>(DynamicString.class));

        // When
        analyzer.handle(propertyWithAutoCompleteContributor, builder, context);

        // Then

        PluginAssertion.assertThat(builder.build()).hasAutoCompleteContributorDefinition(
                AutoCompleteContributorDefinitionMatchers.has(true, true, false, Arrays.asList("")));
    }
}
