package de.codecentric.reedelk.plugin.message;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static de.codecentric.reedelk.plugin.message.ReedelkBundle.message;
import static org.assertj.core.api.Assertions.assertThat;

class ReedelkBundleTest {

    @Test
    void shouldCorrectlyFormatScriptFunctionWithArguments() {
        // Given
        String expectedFunction = "def myFunction(context,message) {\n" +
                "\tdef text = 'myFunction function'\n" +
                "\treturn text + ' result'\n" +
                "}";
        List<String> scriptFunctionArguments = Arrays.asList("context","message");
        String joinedArguments = String.join(",", scriptFunctionArguments);

        // When
        String message = message("script.default.template","myFunction", joinedArguments , "myFunction");

        // Then
        assertThat(message).isEqualTo(expectedFunction);
    }

    @Test
    void shouldCorrectlyFormatScriptFunctionWithoutArguments() {
        // Given
        String expectedFunction = "def myFunction() {\n" +
                "\tdef text = 'myFunction function'\n" +
                "\treturn text + ' result'\n" +
                "}";
        List<String> scriptFunctionArguments = Collections.emptyList();
        String joinedArguments = String.join(",", scriptFunctionArguments);

        // When
        String message = message("script.default.template", "myFunction", joinedArguments , "myFunction");

        // Then
        assertThat(message).isEqualTo(expectedFunction);
    }

    @Test
    void shouldCorrectlyFormatInteger() {
        // When
        String message = message("router.sync.condition.expected.one.successor", 2);

        // Then
        assertThat(message).isEqualTo("Expected at least one successor for router node but [2] were found");
    }

    @Test
    void shouldCorrectlyFormatModuleCheckErrorsContent() {
        // When
        String message = message("module.check.errors.module.errors.content", "http://localhost:1111/console", "Error 1, Error 2");

        // When
        assertThat(message).isEqualTo("Some flow(s) could not be started: go to <a href=\"http://localhost:1111/console\">Reedelk Administration Console</a> and click on the module <b>Info</b> button to view full error details.<br> <b>Error messages:</b> Error 1, Error 2");
    }
}
