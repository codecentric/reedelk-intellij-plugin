package com.reedelk.plugin.service.module.impl.component.scanner.property;

import com.reedelk.plugin.component.domain.AutoCompleteContributorDefinition;
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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

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
        Optional<AutoCompleteContributorDefinition> autoCompleteDefinition =
                builder.build().getAutoCompleteContributorDefinition();
        assertThat(autoCompleteDefinition).isPresent();

        AutoCompleteContributorDefinition definition = autoCompleteDefinition.get();
        assertThat(definition.isError()).isFalse();
        assertThat(definition.isContext()).isTrue();
        assertThat(definition.isMessage()).isTrue();
        definition.getContributions();
    }
}
