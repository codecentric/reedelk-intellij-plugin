package com.reedelk.plugin.action.openapi;

import com.intellij.openapi.project.Project;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class OpenApiImporterContextTest {

    @Mock
    private Project project;

    private final String openAPIFilePath = "/var/etc/openapi.json";
    private final String importModuleName = "my-module";
    private final String targetDirectory = "/Users/Desktop/my-project/src/main/resources";
    private final String apiFileUrl = "http://my-api.com/openapi.json";
    private final Integer openApiPort = 8989;
    private final String basePath = "/v2";

    private OpenApiImporterContext openApiImporterContext;

    @BeforeEach
    void setUp() {
        openApiImporterContext = new OpenApiImporterContext(
                project,
                openAPIFilePath,
                importModuleName,
                targetDirectory,
                apiFileUrl,
                openApiPort,
                basePath);
    }

    @Test
    void shouldReturnContentFormatAsXML() {
        // Given
        String content = "<book xslt:ns=\"http://test.com\">\n" +
                "<title>Hello</title>\n" +
                "</book>";
        // When
        OpenApiExampleFormat format = openApiImporterContext.exampleFormatOf(content);

        // Then
        assertThat(format).isEqualTo(OpenApiExampleFormat.XML);
    }

    @Test
    void shouldReturnContentFormatAsJSON() {
        // Given
        String content = "{ \"name\": \"John\", \"surname\": \"Doe\" }";

        // When
        OpenApiExampleFormat format = openApiImporterContext.exampleFormatOf(content);

        // Then
        assertThat(format).isEqualTo(OpenApiExampleFormat.JSON);
    }

    @Test
    void shouldReturnContentFormatAsTextPlain() {
        // Given
        String content = "Hello this is my example";

        // When
        OpenApiExampleFormat format = openApiImporterContext.exampleFormatOf(content);

        // Then
        assertThat(format).isEqualTo(OpenApiExampleFormat.PLAIN_TEXT);
    }
}
