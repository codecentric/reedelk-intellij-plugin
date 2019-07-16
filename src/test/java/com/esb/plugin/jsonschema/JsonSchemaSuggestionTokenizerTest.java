package com.esb.plugin.jsonschema;

import com.esb.plugin.javascript.Type;
import com.esb.plugin.jsonschema.JsonSchemaSuggestionTokenizer.SchemaDescriptor;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

class JsonSchemaSuggestionTokenizerTest {

    @Test
    void shouldCorrectlyCreateSuggestionTokensForJsonSchema() {
        try (InputStream personSchemaInputStream =
                     JsonSchemaSuggestionTokenizerTest.class.getResourceAsStream("/person.schema.json")) {
            // When
            JsonSchemaSuggestionTokenizer tokenizer = new JsonSchemaSuggestionTokenizer(null, null);
            SchemaDescriptor descriptor = tokenizer.findJsonSchemaTokens("input", personSchemaInputStream);

            // Then
            assertThat(descriptor.getTokens()).containsExactlyInAnyOrder(
                    "input",
                    "input.firstName",
                    "input.lastName",
                    "input.address.zip",
                    "input.address.country",
                    "input.address.city",
                    "input.age");
            assertThat(descriptor.getType()).isEqualTo(Type.OBJECT);
        } catch (IOException e) {
            fail("Error", e);
        }
    }
}