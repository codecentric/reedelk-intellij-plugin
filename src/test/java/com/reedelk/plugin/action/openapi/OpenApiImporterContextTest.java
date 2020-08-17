package com.reedelk.plugin.action.openapi;

import com.intellij.openapi.project.Project;
import com.reedelk.openapi.v3.model.RequestBodyObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

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

    private OpenApiImporterContext context;

    @BeforeEach
    void setUp() {
        context = new OpenApiImporterContext(
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
        OpenApiExampleFormat format = context.exampleFormatOf(content);

        // Then
        assertThat(format).isEqualTo(OpenApiExampleFormat.XML);
    }

    @Test
    void shouldReturnContentFormatAsJSON() {
        // Given
        String content = "{ \"name\": \"John\", \"surname\": \"Doe\" }";

        // When
        OpenApiExampleFormat format = context.exampleFormatOf(content);

        // Then
        assertThat(format).isEqualTo(OpenApiExampleFormat.JSON);
    }

    @Test
    void shouldReturnContentFormatAsTextPlain() {
        // Given
        String content = "Hello this is my example";

        // When
        OpenApiExampleFormat format = context.exampleFormatOf(content);

        // Then
        assertThat(format).isEqualTo(OpenApiExampleFormat.PLAIN_TEXT);
    }

    @Test
    void shouldRegisterRequestBodyCorrectly() {
        // Given
        String requestBodyId = "MyBody";
        RequestBodyObject requestBodyObject = new RequestBodyObject();

        // When
        context.registerRequestBody(requestBodyId, requestBodyObject);
        RequestBodyObject actual = context.getRequestBodyById(requestBodyId);

        // Then
        assertThat(actual).isEqualTo(requestBodyObject);
    }

    @Test
    void shouldRegisterSchemaAssetCorrectly() {
        // Given
        String schemaId = "MySchemaId";
        String schemaAssetPath = "asset/my-api/com.test.my.schema.json";

        // When
        context.registerAssetPath(schemaId, schemaAssetPath);
        Optional<String> actual = context.getAssetFrom(schemaId);

        // Then
        assertThat(actual).contains(schemaAssetPath);
    }

    @Test
    void shouldSetCorrectSchemaFormatYAML() throws OpenApiException {
        // Given
        String content = "openapi: 3.0.0\n" +
                "info:\n" +
                "  description: \"This is a sample server Petstore server.\"\n" +
                "  version: 1.0.2";

        // When
        context.setSchemaFormat(content);
        OpenApiSchemaFormat actual = context.getSchemaFormat();

        // Then
        assertThat(actual).isEqualTo(OpenApiSchemaFormat.YAML);
    }

    @Test
    void shouldSetCorrectSchemaFormatJSON() throws OpenApiException {
        // Given
        String content = "{\n" +
                "  \"openapi\": \"3.0.0\",\n" +
                "  \"info\": {\n" +
                "    \"description\": \"This is a sample server Petstore server.\"\n" +
                "  }\n" +
                "}";

        // When
        context.setSchemaFormat(content);
        OpenApiSchemaFormat actual = context.getSchemaFormat();

        // Then
        assertThat(actual).isEqualTo(OpenApiSchemaFormat.JSON);
    }
}
