package de.codecentric.reedelk.plugin.commons;

import de.codecentric.reedelk.plugin.commons.FileUtils;
import de.codecentric.reedelk.plugin.exception.PluginException;
import de.codecentric.reedelk.runtime.commons.FileExtension;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FileUtilsTest {

    @Test
    void shouldAppendExtensionToFileName() {
        // Given
        String fileName = "MyScript";

        // When
        String actualFileName = FileUtils.appendExtensionToFileName(fileName, FileExtension.SCRIPT);

        // Then
        assertThat(actualFileName).isEqualTo("MyScript.groovy");
    }

    @Test
    void shouldAppendExtensionIfFileNameContainsADifferentExtension() {
        // Given
        String fileName = "MyScript.java";

        // When
        String actualFileName = FileUtils.appendExtensionToFileName(fileName, FileExtension.SCRIPT);

        // Then
        assertThat(actualFileName).isEqualTo("MyScript.java.groovy");
    }

    @Test
    void shouldNotAppendExtensionToFileName() {
        // Given
        String fileName = "MyScript.groovy";

        // When
        String actualFileName = FileUtils.appendExtensionToFileName(fileName, FileExtension.SCRIPT);

        // Then
        assertThat(actualFileName).isEqualTo("MyScript.groovy");
    }

    @Test
    void shouldThrowExceptionIfFileNameIsEmpty() {
        // Given
        String fileName = " ";

        // When
        PluginException exception = assertThrows(PluginException.class, () ->
                FileUtils.appendExtensionToFileName(fileName, FileExtension.SCRIPT));

        // Then
        assertThat(exception).hasMessage("File name must not be empty");
    }
}
