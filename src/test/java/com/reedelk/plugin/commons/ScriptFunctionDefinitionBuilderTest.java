package com.reedelk.plugin.commons;

import com.reedelk.module.descriptor.model.ScriptSignatureDescriptor;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static com.reedelk.plugin.testutils.ObjectFactories.createScriptSignatureDefinition;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

class ScriptFunctionDefinitionBuilderTest {

    @Test
    void shouldBuildCorrectlyBuildFunctionDefinitionWhenArgumentsGreaterThanZero() {
        // Given
        String expectedFunction = "def myTestFunction(context, arg1, arg2, arg3) {\n" +
                "\tdef text = 'myTestFunction function'\n" +
                "\treturn text + ' result'\n" +
                "}";
        String  scriptFunctionName = "myTestFunction";
        ScriptSignatureDescriptor definition =
                createScriptSignatureDefinition(asList("context", "arg1", "arg2", "arg3"));

        // When
        String scriptFunctionDefinition = ScriptFunctionDefinitionBuilder.from(scriptFunctionName, definition);

        // Then
        assertThat(scriptFunctionDefinition).isEqualTo(expectedFunction);
    }

    @Test
    void shouldCorrectlyBuildFunctionDefinitionWhenArgumentsAreEmpty() {
        // Given
        String expectedFunction = "def myTestFunction() {\n" +
                "\tdef text = 'myTestFunction function'\n" +
                "\treturn text + ' result'\n" +
                "}";
        String  scriptFunctionName = "myTestFunction";
        ScriptSignatureDescriptor definition =
                createScriptSignatureDefinition(Collections.emptyList());

        // When
        String scriptFunctionDefinition = ScriptFunctionDefinitionBuilder.from(scriptFunctionName, definition);

        // Then
        assertThat(scriptFunctionDefinition).isEqualTo(expectedFunction);
    }
}
