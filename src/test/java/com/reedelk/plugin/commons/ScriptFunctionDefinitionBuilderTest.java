package com.reedelk.plugin.commons;

import com.reedelk.plugin.component.domain.ScriptSignatureDefinition;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

class ScriptFunctionDefinitionBuilderTest {

    @Test
    void shouldBuildCorrectlyBuildFunctionDefinitionWhenArgumentsGreaterThanZero() {
        // Given
        String expectedFunction = "function myTestFunction(context,arg1,arg2,arg3) {\n" +
                "\tvar text = 'myTestFunction function';\n" +
                "\treturn text + ' result';\n" +
                "}";
        String  scriptFunctionName = "myTestFunction";
        ScriptSignatureDefinition definition =
                new ScriptSignatureDefinition(Arrays.asList("context", "arg1", "arg2", "arg3"));

        // When
        String scriptFunctionDefinition = ScriptFunctionDefinitionBuilder.from(scriptFunctionName, definition);

        // Then
        assertThat(scriptFunctionDefinition).isEqualTo(expectedFunction);
    }

    @Test
    void shouldCorrectlyBuildFunctionDefinitionWhenArgumentsAreEmpty() {
        // Given
        String expectedFunction = "function myTestFunction() {\n" +
                "\tvar text = 'myTestFunction function';\n" +
                "\treturn text + ' result';\n" +
                "}";
        String  scriptFunctionName = "myTestFunction";
        ScriptSignatureDefinition definition =
                new ScriptSignatureDefinition(Collections.emptyList());

        // When
        String scriptFunctionDefinition = ScriptFunctionDefinitionBuilder.from(scriptFunctionName, definition);

        // Then
        assertThat(scriptFunctionDefinition).isEqualTo(expectedFunction);
    }
}