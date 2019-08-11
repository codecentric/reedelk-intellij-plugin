package com.reedelk.plugin.jsonschema;

import com.reedelk.plugin.fixture.JsonSchema;
import org.everit.json.schema.loader.SchemaClient;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class JsonSchemaSuggestionsProcessorTest {

    @Mock
    private SchemaClient mockSchemaClient;

    @Test
    void shouldCorrectlyExtractTokensForSimpleSchema() {
        // Given
        String simpleJsonSchema = JsonSchema.SIMPLE.json();
        JSONObject jsonSchemaObject = new JSONObject(simpleJsonSchema);
        JsonSchemaSuggestionsProcessor processor = new JsonSchemaSuggestionsProcessor(jsonSchemaObject, mockSchemaClient);

        // When
        JsonSchemaSuggestionsResult result = processor.process();

        // Then
        List<String> actualTokens = result.getTokens();
        assertThat(actualTokens).containsExactlyInAnyOrder(
                "property1",
                "property2",
                "property3");
    }

    @Test
    void shouldCorrectlyExtractTokensForSchemaWithReference() {
        // Given
        String referencedSchemaJson = JsonSchema.REFERENCED.json();
        InputStream inputStream = new ByteArrayInputStream(referencedSchemaJson.getBytes());
        doReturn(inputStream)
                .when(mockSchemaClient)
                .get("https://reedelk.com/referenced.schema.json");

        String schemaWithReference = JsonSchema.WITH_REFERENCE.json();
        JSONObject jsonSchemaObject = new JSONObject(schemaWithReference);
        JsonSchemaSuggestionsProcessor processor = new JsonSchemaSuggestionsProcessor(jsonSchemaObject, mockSchemaClient);

        // When
        JsonSchemaSuggestionsResult result = processor.process();

        // Then
        List<String> actualTokens = result.getTokens();
        assertThat(actualTokens).containsExactlyInAnyOrder(
                "firstName",
                "lastName",
                "age",
                "address",
                "address.city",
                "address.country",
                "address.zip");
    }
}