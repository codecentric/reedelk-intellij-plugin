package com.reedelk.plugin.service.application.impl.completion;

import com.reedelk.plugin.executor.PluginExecutor;
import com.reedelk.plugin.message.SuggestionsBundle;
import com.reedelk.plugin.service.application.CompletionService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class CompletionServiceImpl implements CompletionService {

    private final Trie trie;

    private boolean initializing;

    public CompletionServiceImpl() {
        this.trie = new Trie();
        PluginExecutor.getInstance().submit(() -> {
            initializing = true;
            initialize();
            initializing = false;
        });
    }

    @Override
    public Optional<List<Suggestion>> completionTokensOf(String token) {
        return initializing ? Optional.empty() : trie.findByPrefix(token);
    }

    // TODO: Update the Scripts when maven imported and analyze fields

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