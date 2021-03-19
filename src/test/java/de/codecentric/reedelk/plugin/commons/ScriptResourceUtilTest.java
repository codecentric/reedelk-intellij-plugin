package de.codecentric.reedelk.plugin.commons;

import de.codecentric.reedelk.plugin.commons.ScriptResourceUtil;
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
        assertThat(normalized).isEqualTo("dir1/dir2/function.groovy");
    }
}
