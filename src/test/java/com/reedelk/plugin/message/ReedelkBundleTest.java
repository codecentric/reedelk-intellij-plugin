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
        String expectedFunction = "function myFunction(context,message) {\n" +
                "\tvar text = 'myFunction function';\n" +
                "\treturn text + ' result';\n" +
                "}";
        List<String> scriptFunctionArguments = Arrays.asList("context","message");
        String joinedArguments = String.join(",", scriptFunctionArguments);

        // When
        String message = ReedelkBundle.message("script.default.template","myFunction", joinedArguments , "myFunction");

        // Then
        assertThat(message).isEqualTo(expectedFunction);
    }

    @Test
    void shouldCorrectlyFormatScriptFunctionWithoutArguments() {
        // Given
        String expectedFunction = "function myFunction() {\n" +
                "\tvar text = 'myFunction function';\n" +
                "\treturn text + ' result';\n" +
                "}";
        List<String> scriptFunctionArguments = Collections.emptyList();
        String joinedArguments = String.join(",", scriptFunctionArguments);

        // When
        String message = ReedelkBundle.message("script.default.template", "myFunction", joinedArguments , "myFunction");

        // Then
        assertThat(message).isEqualTo(expectedFunction);
    }

    @Test
    void shouldCorrectlyFormatInteger() {
        // When
        String message = ReedelkBundle.message("router.sync.condition.expected.one.successor", 2);

        // Then
        assertThat(message).isEqualTo("Expected at least one successor for router node but [2] were found");
    }
}