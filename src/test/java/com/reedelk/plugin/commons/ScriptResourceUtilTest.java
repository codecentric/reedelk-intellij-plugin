package com.reedelk.plugin.commons;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ScriptResourceUtilTest {

    @Test
    void shouldNormalizeFileCorrectly() {
        // Given
        String scriptFilename = "/dir1/dir2/function";

        // When
        String normalized = ScriptResourceUtil.normalize(scriptFilename);

        // Then
        assertThat(normalized).isEqualTo("dir1/dir2/function.js");
    }
}