package com.reedelk.plugin.service.module.impl.completion;

class TrieTest {

    private final String suggestionToken1 = "message[VARIABLE:Message]";
    private final String suggestionToken2 = "multiply()[FUNCTION:int]";
    private final String suggestionToken3 = "message.mimeType1()[FUNCTION:MimeType]";
    private final String suggestionToken4 = "message.mimeType2()[FUNCTION:MimeType]";
    private final String suggestionToken5 = "message.mimeType3()[FUNCTION:MimeType]";
    private final String suggestionToken6 = "message.put('',item)[FUNCTION:MimeType:7]";

    /**
    @Test
    void shouldCorrectlyInsertSuggestionToken() {
        // Given
        Trie trie = new Trie();

        // When
        trie.insert(suggestionToken1);

        // Then
        List<Suggestion> results = trie.findByPrefix("m");
        PluginAssertion.assertThat(results)
                .containsOnly("message", SuggestionType.VARIABLE, "Message");
    }

    @Test
    void shouldCorrectlyFindMultipleSuggestionsByPrefix() {
        // Given
        Trie trie = new Trie();

        // When
        trie.insert(asList(suggestionToken1, suggestionToken2));

        // Then
        List<Suggestion> results = trie.findByPrefix("m");
        PluginAssertion.assertThat(results)
                .contains("message", SuggestionType.VARIABLE, "Message")
                .contains("multiply()", SuggestionType.FUNCTION, "int")
                .hasSize(2);
    }

    @Test
    void shouldCorrectlyFindMultipleSuggestionsByPrefixAfterDot() {
        // Given
        Trie trie = new Trie();

        // When
        trie.insert(suggestionToken1, suggestionToken2, suggestionToken3, suggestionToken4, suggestionToken5);

        // Then
        List<Suggestion> results = trie.findByPrefix("message.mim");
        PluginAssertion.assertThat(results)
                .contains("mimeType1()", SuggestionType.FUNCTION, "MimeType")
                .contains("mimeType2()", SuggestionType.FUNCTION, "MimeType")
                .contains("mimeType3()", SuggestionType.FUNCTION, "MimeType")
                .hasSize(3);
    }

    @Test
    void shouldCorrectlyFindMultipleSuggestionsByPrefixWhenSearchPrefixEndsWithDot() {
        // Given
        Trie trie = new Trie();

        // When
        trie.insert(suggestionToken1, suggestionToken2, suggestionToken3, suggestionToken4, suggestionToken5);

        // Then
        List<Suggestion> results = trie.findByPrefix("message.");
        PluginAssertion.assertThat(results)
                .contains("mimeType1()", SuggestionType.FUNCTION, "MimeType")
                .contains("mimeType2()", SuggestionType.FUNCTION, "MimeType")
                .contains("mimeType3()", SuggestionType.FUNCTION, "MimeType")
                .hasSize(3);
    }

    @Test
    void shouldCorrectlyFindMultipleSuggestionsByPrefixWhenSearchPrefixEndsWithFunction() {
        // Given
        Trie trie = new Trie();

        // When
        trie.insert(suggestionToken1, suggestionToken2, suggestionToken3, suggestionToken4, suggestionToken5);

        // Then
        List<Suggestion> results = trie.findByPrefix("message.mimeType1()");
        PluginAssertion.assertThat(results).isEmpty();
    }

    @Test
    void shouldCorrectFindAllRootSuggestionsWhenPrefixIsEmptyString() {
        // Given
        Trie trie = new Trie();

        // When
        trie.insert(asList(suggestionToken1, suggestionToken2, suggestionToken3, suggestionToken4, suggestionToken5));

        // Then
        List<Suggestion> results = trie.findByPrefix("");

        PluginAssertion.assertThat(results)
                .contains("message", SuggestionType.VARIABLE, "Message")
                .contains("multiply()", SuggestionType.FUNCTION, "int")
                .hasSize(2);
    }

    @Test
    void shouldReturnEmptyWhenPrefixEndsWithOpenParenthesis() {
        // Given
        Trie trie = new Trie();

        // When
        trie.insert(suggestionToken1, suggestionToken2, suggestionToken3, suggestionToken4, suggestionToken5);

        // Then
        List<Suggestion> results = trie.findByPrefix("message.mimeType1(");
        PluginAssertion.assertThat(results).isEmpty();
    }

    @Test
    void shouldCorrectlyReturnPut() {
        // Given
        Trie trie = new Trie();

        // When
        trie.insert(suggestionToken6);

        // Then
        List<Suggestion> results = trie.findByPrefix("message.pu");
        PluginAssertion.assertThat(results).hasSize(1);
    }*/
}
