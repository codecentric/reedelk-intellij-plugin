package com.esb.plugin.component.scanner.property;

import com.esb.plugin.component.domain.AutocompleteVariable;
import com.esb.plugin.component.domain.ComponentPropertyDescriptor;
import com.esb.plugin.component.domain.TypeDescriptor;
import com.esb.plugin.component.scanner.ComponentAnalyzerContext;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.FieldInfo;
import io.github.classgraph.ScanResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

class PropertyAutocompleteVariableHandlerTest {

    private ComponentAnalyzerContext context;
    private ClassInfo componentWithAutocompleteVariable;
    private PropertyAutocompleteVariableHandler handler = new PropertyAutocompleteVariableHandler();

    @BeforeEach
    void setUp() {
        ScanResult scanResult = new ClassGraph()
                .whitelistPackages(PropertyAutocompleteVariableHandlerTest.class.getPackage().getName())
                .enableFieldInfo()
                .enableAnnotationInfo()
                .ignoreFieldVisibility()
                .scan();

        this.context = new ComponentAnalyzerContext(scanResult);
        this.componentWithAutocompleteVariable = scanResult.getClassInfo(ComponentWithAutocompleteVariable.class.getName());
    }

    @Test
    void shouldCorrectlyMapAutocompleteVariablesAnnotations() {
        // Given
        FieldInfo scriptField = componentWithAutocompleteVariable.getFieldInfo("script");

        // When
        ComponentPropertyDescriptor.Builder builder = ComponentPropertyDescriptor.builder();
        builder.propertyName("notRelevantPropertyName").type(new NotRelevantPropertyType());

        handler.handle(scriptField, builder, context);

        // Then
        ComponentPropertyDescriptor descriptor = builder.build();
        List<AutocompleteVariable> variables = descriptor.getAutocompleteVariables();
        assertThat(variables).hasSize(2);

        assertExistsAutocompleteVariableMatching(variables, "input", "inputContext", "{}");
        assertExistsAutocompleteVariableMatching(variables, "output", "outputContext", "{}");
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