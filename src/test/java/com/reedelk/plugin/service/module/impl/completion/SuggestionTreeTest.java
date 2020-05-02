package com.reedelk.plugin.service.module.impl.completion;

class SuggestionTreeTest {

    /**
    @Test
    void shouldReturnEmptyWhenNoMatchesAreFound() {
        // Given
        SuggestionTree suggestionTree = new SuggestionTree(new HashMap<>());
        List<Suggestion> suggestions = asList(SampleSuggestions.UTIL, SampleSuggestions.CONFIG);
        suggestionTree.add(suggestions);

        // When
        List<Suggestion> actualSuggestions = suggestionTree.autocomplete(new String[]{"message", ""});

        // Then
        PluginAssertion.assertThat(actualSuggestions)
                .isEmpty();
    }

    @Test
    void shouldReturnCorrectTokenWhenRootMatches() {
        // Given
        SuggestionTree suggestionTree = new SuggestionTree(new HashMap<>());
        List<Suggestion> autocompleteItems = asList(SampleSuggestions.UTIL, SampleSuggestions.CONFIG);
        suggestionTree.add(autocompleteItems);

        // When
        List<Suggestion> suggestions = suggestionTree.autocomplete(new String[] {"Ut"});

        // Then
        PluginAssertion.assertThat(suggestions)
                .hasSize(1)
                .contains(SampleSuggestions.UTIL);
    }

    @Test
    void shouldReturnAllRootVariablesWhenSuggestionTokenIsEmpty() {
        // Given
        SuggestionTree suggestionTree = new SuggestionTree(new HashMap<>());
        List<Suggestion> autocompleteItems = asList(SampleSuggestions.UTIL, SampleSuggestions.CONFIG);
        suggestionTree.add(autocompleteItems);

        // When
        List<Suggestion> suggestions = suggestionTree.autocomplete(new String[]{""});

        // Then
        PluginAssertion.assertThat(suggestions)
                .hasSize(2)
                .contains(SampleSuggestions.UTIL)
                .contains(SampleSuggestions.CONFIG);
    }

    @Test
    void shouldReturnEmptyResultsWhenNoSuggestionsAndTokenIsEmpty() {
        // Given
        SuggestionTree suggestionTree = new SuggestionTree(new HashMap<>());

        // When
        List<Suggestion> suggestions = suggestionTree.autocomplete(new String[]{""});

        // Then
        PluginAssertion.assertThat(suggestions)
                .isEmpty();
    }

    @Test
    void shouldReturnMultipleSuggestion() {
        // Given
        SuggestionTree suggestionTree = new SuggestionTree(new HashMap<>());
        List<Suggestion> autocompleteItems = asList(SampleSuggestions.UTIL, SampleSuggestions.TMP_DIR, SampleSuggestions.UUID);
        suggestionTree.add(autocompleteItems);

        // When
        List<Suggestion> autocomplete = suggestionTree.autocomplete(new String[]{"Util", ""});

        // Then
        PluginAssertion.assertThat(autocomplete)
                .hasSize(2)
                .contains(SampleSuggestions.TMP_DIR)
                .contains(SampleSuggestions.UUID);
    }

    @Test
    void shouldReturnCorrectSuggestion() {
        // Given
        SuggestionTree suggestionTree = new SuggestionTree(new HashMap<>());
        List<Suggestion> autocompleteItems = asList(SampleSuggestions.UTIL, SampleSuggestions.TMP_DIR, SampleSuggestions.UUID);
        suggestionTree.add(autocompleteItems);

        // When
        List<Suggestion> autocomplete = suggestionTree.autocomplete(new String[]{"Util","u"});

        // Then
        PluginAssertion.assertThat(autocomplete)
                .hasSize(1)
                .contains(SampleSuggestions.UUID);
    }

    @Test
    void shouldReturnEmptySuggestions() {
        // Given
        SuggestionTree suggestionTree = new SuggestionTree(new HashMap<>());
        List<Suggestion> autocompleteItems = asList(SampleSuggestions.UTIL, SampleSuggestions.TMP_DIR);
        suggestionTree.add(autocompleteItems);

        // When
        List<Suggestion> autocomplete = suggestionTree.autocomplete(new String[]{"U", "tm"});

        // Then
        PluginAssertion.assertThat(autocomplete).isEmpty();
    }*/
}
