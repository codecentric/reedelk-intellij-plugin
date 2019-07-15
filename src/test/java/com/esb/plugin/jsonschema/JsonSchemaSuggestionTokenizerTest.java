package com.esb.plugin.jsonschema;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JsonSchemaSuggestionTokenizerTest {

    @Test
    void shouldDoSomething() {
        // Given

        // When
        JsonSchemaSuggestionTokenizer parser = new JsonSchemaSuggestionTokenizer();
        JsonSchemaSuggestionTokenizer.SchemaDescriptor descriptor = parser.read("/person.schema.json");

        // Then
        assertThat(descriptor.getTokens()).hasSize(3);
        assertThat(descriptor.getType()).isEqualTo("Person");
    }
}