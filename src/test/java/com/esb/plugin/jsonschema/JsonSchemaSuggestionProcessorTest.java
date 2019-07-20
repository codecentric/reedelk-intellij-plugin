package com.esb.plugin.jsonschema;

import org.junit.jupiter.api.Test;

class JsonSchemaSuggestionProcessorTest {

    // TODO: Fixme
    @Test
    void shouldCorrectlyCreateSuggestionTokensForJsonSchema() {
        /**
         try (InputStream personSchemaInputStream =
         JsonSchemaSuggestionProcessorTest.class.getResourceAsStream("/person.schema.json")) {
         // When
         JsonSchemaSuggestionProcessor tokenizer = new JsonSchemaSuggestionProcessor(null, null, null, null);
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