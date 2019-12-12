package com.reedelk.plugin.message;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ReedelkBundleTest {

    @Test
    void shouldCorrectlyFormatScriptFunctionWithArguments() {
        // Given
        String expectedFunction = "function run(context,message) {\n" +
                "\tvar result = 'myScript script result';\n" +
                "\treturn result;\n" +
                "}";
        List<String> scriptFunctionArguments = Arrays.asList("context","message");
        String joinedArguments = String.join(",", scriptFunctionArguments);

        // When
        String message = ReedelkBundle.message("script.default.template",joinedArguments , "myScript");

        // Then
        assertThat(message).isEqualTo(expectedFunction);
    }

    @Test
    void shouldCorrectlyFormatScriptFunctionWithoutArguments() {
        // Given
        String expectedFunction = "function run() {\n" +
                "\tvar result = 'myScript script result';\n" +
                "\treturn result;\n" +
                "}";
        List<String> scriptFunctionArguments = Collections.emptyList();
        String joinedArguments = String.join(",", scriptFunctionArguments);

        // When
        String message = ReedelkBundle.message("script.default.template",joinedArguments , "myScript");

        // Then
        assertThat(message).isEqualTo(expectedFunction);
    }
}