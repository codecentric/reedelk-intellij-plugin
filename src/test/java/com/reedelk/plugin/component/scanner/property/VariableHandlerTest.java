package com.reedelk.plugin.component.scanner.property;

import com.reedelk.plugin.component.domain.ComponentPropertyDescriptor;
import com.reedelk.plugin.component.domain.TypeDescriptor;
import com.reedelk.plugin.component.domain.VariableDefinition;
import com.reedelk.plugin.component.scanner.AbstractScannerTest;
import com.reedelk.plugin.component.scanner.ComponentAnalyzerContext;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.FieldInfo;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

class VariableHandlerTest extends AbstractScannerTest {

    private VariableHandler handler = new VariableHandler();

    private static ClassInfo componentClassInfo;
    private static ComponentAnalyzerContext context;

    @BeforeAll
    static void beforeAll() {
        ScanContext scanContext = scan(ComponentWithAutocompleteVariables.class);
        context = scanContext.context;
        componentClassInfo = scanContext.targetComponentClassInfo;
    }

    @Test
    void shouldCorrectlyMapAutocompleteVariablesAnnotations() {
        // Given
        FieldInfo scriptField = componentClassInfo.getFieldInfo("script");

        // When
        ComponentPropertyDescriptor.Builder builder = ComponentPropertyDescriptor.builder();
        builder.propertyName("notRelevantPropertyName").type(new NotRelevantPropertyType());

        handler.handle(scriptField, builder, context);

        // Then
        ComponentPropertyDescriptor descriptor = builder.build();
        List<VariableDefinition> variables = descriptor.getVariableDefinitions();
        assertThat(variables).hasSize(2);

        assertExistsAutocompleteVariableMatching(variables, "input", "inputContext", "{}");
        assertExistsAutocompleteVariableMatching(variables, "output", "outputContext", "{}");
    }

    private void assertExistsAutocompleteVariableMatching(List<VariableDefinition> collection, String expectedVariableName, String expectedContextName, String expectedInitValue) {
        for (VariableDefinition variable : collection) {
            String actualContextName = variable.getContextName();
            String actualVariableName = variable.getVariableName();
            if (expectedVariableName.equals(actualVariableName) &&
                    expectedContextName.equals(actualContextName)) return;
        }
        fail("Could not find Autocomplete Variable definition matching variable name=%s, context name=%s, init value=%s");
    }

    class NotRelevantPropertyType implements TypeDescriptor {

        @Override
        public Class<?> type() {
            return int.class;
        }

        @Override
        public Object defaultValue() {
            return 12;
        }
    }

}