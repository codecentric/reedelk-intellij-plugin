package de.codecentric.reedelk.plugin.commons;

import de.codecentric.reedelk.module.descriptor.model.property.ScriptSignatureArgument;
import de.codecentric.reedelk.module.descriptor.model.property.ScriptSignatureDescriptor;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static de.codecentric.reedelk.plugin.testutils.ObjectFactories.createScriptSignatureDefinition;
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

        List<ScriptSignatureArgument> descriptor = Arrays.asList(
                new ScriptSignatureArgument("context", "Type1"),
                new ScriptSignatureArgument("arg1", "Type2"),
                new ScriptSignatureArgument("arg2", "Type3"),
                new ScriptSignatureArgument("arg3", "Type4"));
        ScriptSignatureDescriptor definition = createScriptSignatureDefinition(descriptor);

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
