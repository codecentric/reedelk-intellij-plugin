package com.esb.plugin.component.scanner.property;

import com.esb.plugin.component.domain.AutocompleteVariable;
import com.esb.plugin.component.domain.ComponentPropertyDescriptor;
import com.esb.plugin.component.domain.TypeDescriptor;
import com.esb.plugin.component.scanner.AbstractScannerTest;
import io.github.classgraph.FieldInfo;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

class PropertyAutocompleteVariableHandlerTest extends AbstractScannerTest {

    private PropertyAutocompleteVariableHandler handler = new PropertyAutocompleteVariableHandler();

    @Test
    void shouldCorrectlyMapAutocompleteVariablesAnnotations() {
        // Given
        FieldInfo scriptField = getTargetComponentClassInfo().getFieldInfo("script");

        // When
        ComponentPropertyDescriptor.Builder builder = ComponentPropertyDescriptor.builder();
        builder.propertyName("notRelevantPropertyName").type(new NotRelevantPropertyType());

        handler.handle(scriptField, builder, context());

        // Then
        ComponentPropertyDescriptor descriptor = builder.build();
        List<AutocompleteVariable> variables = descriptor.getAutocompleteVariables();
        assertThat(variables).hasSize(2);

        assertExistsAutocompleteVariableMatching(variables, "input", "inputContext", "{}");
        assertExistsAutocompleteVariableMatching(variables, "output", "outputContext", "{}");
    }

    @Override
    protected Class targetComponentClazz() {
        return ComponentWithAutocompleteVariables.class;
    }

    private void assertExistsAutocompleteVariableMatching(List<AutocompleteVariable> collection, String expectedVariableName, String expectedContextName, String expectedInitValue) {
        for (AutocompleteVariable variable : collection) {
            String actualInitValue = variable.getInitValue();
            String actualContextName = variable.getContextName();
            String actualVariableName = variable.getVariableName();
            if (expectedVariableName.equals(actualVariableName) &&
                    expectedContextName.equals(actualContextName) &&
                    expectedInitValue.equals(actualInitValue)) return;
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