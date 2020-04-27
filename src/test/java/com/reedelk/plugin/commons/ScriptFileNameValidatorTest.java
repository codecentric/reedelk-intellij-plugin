package com.reedelk.plugin.commons;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ScriptFileNameValidatorTest {

    @Test
    void shouldIsValidFunctionNameReturnTrue() {
        // Given
        String functionName = "My_Function";

        // When
        boolean isValid = ScriptFileNameValidator.validate(functionName);

        // Then
        assertThat(isValid).isTrue();
    }

    @Test
    void shouldIsValidFunctionNameReturnFalse() {
        // Given
        String functionName = "1$_myFunction";

        // When
        boolean isValid = ScriptFileNameValidator.validate(functionName);

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    void shouldIsValidReturnFalseWhenFunctionNameEndsWithSlash() {
        // Given
        String functionName = "scripts/";

        // When
        boolean isValid = ScriptFileNameValidator.validate(functionName);

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    void shouldIsValidReturnFalseWhenFunctionNameEndsWithBackSlash() {
        // Given
        String functionName = "scripts\\";

        // When
        boolean isValid = ScriptFileNameValidator.validate(functionName);

        // Then
        assertThat(isValid).isFalse();
    }
}
