package com.reedelk.plugin.service.module.impl.component.scanner.property;

import com.reedelk.plugin.assertion.PluginAssertion;
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

import static com.reedelk.plugin.assertion.component.AutoCompleteContributorDefinitionMatchers.with;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

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
    void shouldCorrectlyCreateAutoCompleteContributorDefinitionWithCustomContributions() {
        // Given
        String propertyName = "propertyWithCustomContributions";
        FieldInfo property = componentClassInfo.getFieldInfo(propertyName);

        ComponentPropertyDescriptor.Builder builder =
                ComponentPropertyDescriptor.builder()
                        .propertyName(propertyName)
                        .type(new TypeDynamicValueDescriptor<>(DynamicString.class));

        // When
        analyzer.handle(property, builder, context);

        // Then
        PluginAssertion.assertThat(builder.build())
                .hasAutoCompleteContributorDefinition(with(true,true,false,
                        asList("messages[VARIABLE:Message[]]","messages.size()[FUNCTION:int]")));
    }

    @Test
    void shouldCorrectlyCreateAutoCompleteContributorDefinitionWithoutMessageContributions() {
        // Given
        String propertyName = "propertyWithoutMessageContributions";
        FieldInfo property = componentClassInfo.getFieldInfo(propertyName);

        ComponentPropertyDescriptor.Builder builder =
                ComponentPropertyDescriptor.builder()
                        .propertyName(propertyName)
                        .type(new TypeDynamicValueDescriptor<>(DynamicString.class));

        // When
        analyzer.handle(property, builder, context);

        // Then
        PluginAssertion.assertThat(builder.build())
                .hasAutoCompleteContributorDefinition(with(false,true,false, emptyList()));
    }

    @Test
    void shouldCorrectlyCreateAutoCompleteContributorDefinitionWithoutContextContributions() {
        // Given
        String propertyName = "propertyWithoutContextContributions";
        FieldInfo property = componentClassInfo.getFieldInfo(propertyName);

        ComponentPropertyDescriptor.Builder builder =
                ComponentPropertyDescriptor.builder()
                        .propertyName(propertyName)
                        .type(new TypeDynamicValueDescriptor<>(DynamicString.class));

        // When
        analyzer.handle(property, builder, context);

        // Then
        PluginAssertion.assertThat(builder.build())
                .hasAutoCompleteContributorDefinition(with(true,false,false, emptyList()));
    }

    @Test
    void shouldCorrectlyCreateAutoCompleteContributorDefinitionWithErrorAndWithoutMessageContributions() {
        // Given
        String propertyName = "propertyWithErrorAndWithoutMessageContributions";
        FieldInfo property = componentClassInfo.getFieldInfo(propertyName);

        ComponentPropertyDescriptor.Builder builder =
                ComponentPropertyDescriptor.builder()
                        .propertyName(propertyName)
                        .type(new TypeDynamicValueDescriptor<>(DynamicString.class));

        // When
        analyzer.handle(property, builder, context);

        // Then
        PluginAssertion.assertThat(builder.build())
                .hasAutoCompleteContributorDefinition(with(false, true,true, emptyList()));
    }

    @Test
    void shouldProvideEmptyAutoCompleteContributorWhenPropertyDoesNotHaveOne() {
        // Given
        String propertyName = "propertyWithoutAutoCompleteContributor";
        FieldInfo property = componentClassInfo.getFieldInfo(propertyName);

        ComponentPropertyDescriptor.Builder builder =
                ComponentPropertyDescriptor.builder()
                        .propertyName(propertyName)
                        .type(new TypeDynamicValueDescriptor<>(DynamicString.class));

        // When
        analyzer.handle(property, builder, context);

        // Then
        PluginAssertion.assertThat(builder.build())
                .hasNotAutoCompleteContributorDefinition();
    }
}
