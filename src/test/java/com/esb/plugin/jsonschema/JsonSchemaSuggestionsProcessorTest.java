package com.esb.plugin.jsonschema;

import org.junit.jupiter.api.Test;

class JsonSchemaSuggestionsProcessorTest {

    // TODO: Fixme
    @Test
    void shouldCorrectlyCreateSuggestionTokensForJsonSchema() {
        /**
         try (InputStream personSchemaInputStream =
         JsonSchemaSuggestionsProcessorTest.class.getResourceAsStream("/person.schema.json")) {
         // When
         JsonSchemaSuggestionsProcessor tokenizer = new JsonSchemaSuggestionsProcessor(null, null, null, null);
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
         }*/
    }
}