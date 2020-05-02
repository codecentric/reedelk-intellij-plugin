package com.reedelk.plugin.service.module.impl.completion;

class TrieTest {

    /**
    @Test
    void shouldCorrectlyInsertSuggestionToken() {
        // Given
        Trie trie = new Trie();

        // When
        trie.insert(SampleSuggestions.UTIL);

        // Then
        List<Suggestion> result = trie.autocomplete("Ut");
        PluginAssertion.assertThat(result)
                .containsOnly(SampleSuggestions.UTIL);
    }

    @Test
    void shouldCorrectlyFindMultipleSuggestionsByPrefix() {
        // Given
        Suggestion suggestion1 = Suggestion.create(Suggestion.Type.PROPERTY)
                .withLookupString("text")
                .withType("com.test.MyType")
                .withPresentableText("text")
                .withCursorOffset(0)
                .build();

        Suggestion suggestion2 = Suggestion.create(Suggestion.Type.FUNCTION)
                .withLookupString("textWithMimeType")
                .withType("com.test.MyType")
                .withPresentableText("textWithMimeType()")
                .withCursorOffset(0)
                .build();

        Trie trie = new Trie();
        trie.insert(suggestion1);
        trie.insert(suggestion2);

        // When
        List<Suggestion> suggestions = trie.autocomplete("te");

        // Then
        PluginAssertion.assertThat(suggestions)
                .hasSize(2)
                .contains(suggestion1)
                .contains(suggestion2);
    }

    @Test
    void shouldCorrectlyAllSuggestionsWhenEmptyString() {
        // Given
        Trie trie = new Trie();
        trie.insert(SampleSuggestions.UTIL);
        trie.insert(SampleSuggestions.CONFIG);

        // When
        List<Suggestion> suggestions = trie.autocomplete("");

        // Then
        PluginAssertion.assertThat(suggestions)
                .hasSize(2)
                .contains(SampleSuggestions.UTIL)
                .contains(SampleSuggestions.CONFIG);
    }

    @Test
    void shouldReturnEmptySuggestions() {
        // Given
        Trie trie = new Trie();
        trie.insert(SampleSuggestions.UTIL);
        trie.insert(SampleSuggestions.CONFIG);

        // When
        List<Suggestion> suggestions = trie.autocomplete("Log");

        // Then
        PluginAssertion.assertThat(suggestions)
                .isEmpty();
    }*/
}
