package com.esb.plugin.jsonschema;

import org.assertj.core.api.Assertions;
import org.everit.json.schema.loader.SchemaClient;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class JsonSchemaSuggestionsProcessorTest {

    @Mock
    private SchemaClient mockSchemaClient;

    @Test
    void shouldCorrectlyCreateSuggestionTokensForJsonSchema() {
        // Given
        JSONObject jsonSchemaObject = new JSONObject();
        JsonSchemaSuggestionsProcessor processor = new JsonSchemaSuggestionsProcessor(jsonSchemaObject, mockSchemaClient);

        // When
        JsonSchemaSuggestionsResult result = processor.read();

        // Then
        List<String> actualTokens = result.getTokens();
        Assertions.assertThat(actualTokens).containsExactlyInAnyOrder(
                "input",
                "input.firstName",
                "input.lastName",
                "input.address.zip",
                "input.address.country",
                "input.address.city",
                "input.age");
    }
}