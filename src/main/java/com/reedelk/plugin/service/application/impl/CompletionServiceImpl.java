package com.reedelk.plugin.service.application.impl;

import com.reedelk.plugin.message.SuggestionsBundle;
import com.reedelk.plugin.service.application.CompletionService;
import com.reedelk.plugin.service.application.impl.completion.Suggestion;
import com.reedelk.plugin.service.application.impl.completion.Trie;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class CompletionServiceImpl implements CompletionService {

    private final Trie trie;

    public CompletionServiceImpl() {
        this.trie = new Trie();
        initialize();
    }

    @Override
    public Optional<List<Suggestion>> completionTokensOf(String token) {
        return trie.findByPrefix(token);
    }

    private void initialize() {
        tokensFrom("message");
        tokensFrom("context");
    }

    private void tokensFrom(String key) {
        String[] tokens = SuggestionsBundle.message(key).split(",");
        Arrays.stream(tokens).forEach(suggestionTokenDefinition -> CompletionService.parseSuggestionToken(suggestionTokenDefinition).ifPresent(parsed ->
                trie.insert(parsed.getMiddle(), parsed.getRight(), parsed.getLeft())));
    }
}