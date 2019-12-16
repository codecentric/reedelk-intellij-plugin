package com.reedelk.plugin.commons;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RuntimeConsoleURLTest {

    @Test
    void shouldCorrectlyGenerateUrl() {
        // Given
        String runtimeHostAddress = "localhost";
        int runtimeHostPort = 1232;

        // When
        String actual = RuntimeConsoleURL.from(runtimeHostAddress, runtimeHostPort);

        // Then
        assertThat(actual).isEqualTo("http://localhost:1232/console");
    }
}