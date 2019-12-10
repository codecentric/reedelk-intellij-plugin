package com.reedelk.plugin.service.module.impl.completion;

import com.reedelk.plugin.commons.SuggestionDefinitionMatcher;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Trie {

    private TrieNode root = new TrieNode();

    public void insert(String suggestionDefinition) {
        SuggestionDefinitionMatcher.of(suggestionDefinition).ifPresent(parsed -> insert(parsed.getMiddle(), parsed.getRight(), parsed.getLeft()));
    }

    public Optional<List<Suggestion>> findByPrefix(String prefix) {
        return find(prefix).map(trieNode -> {
            int i = prefix.lastIndexOf('.');
            String toAppend = i == -1 ? prefix : prefix.substring(i + 1);
            List<Suggestion> matches = allTokensFrom(toAppend, trieNode);
            return Optional.of(matches);
        }).orElse(Optional.empty());
    }

    public void delete(String word) {
        delete(root, word, 0);
    }

    private void insert(SuggestionType type, String typeName, String word) {
        TrieNode current = root;
        for (int i = 0; i < word.length(); i++) {
            current = current.getChildren().computeIfAbsent(word.charAt(i), c -> new TrieNode());
        }
        current.setType(type);
        current.setTypeName(typeName);
        current.setEndOfWord(true);
    }

    private Optional<TrieNode> find(String word) {
        TrieNode current = root;
        for (int i = 0; i < word.length(); i++) {
            char ch = word.charAt(i);
            TrieNode node = current.getChildren().get(ch);
            if (node == null) {
                return Optional.empty();
            }
            current = node;
        }
        return Optional.of(current);
    }

    private boolean delete(TrieNode current, String word, int index) {
        if (index == word.length()) {
            if (!current.isEndOfWord()) {
                return false;
            }
            current.setEndOfWord(false);
            return current.getChildren().isEmpty();
        }
        char ch = word.charAt(index);
        TrieNode node = current.getChildren().get(ch);
        if (node == null) {
            return false;
        }
        boolean shouldDeleteCurrentNode = delete(node, word, index + 1) && !node.isEndOfWord();

        if (shouldDeleteCurrentNode) {
            current.getChildren().remove(ch);
            return current.getChildren().isEmpty();
        }
        return false;
    }


    private List<Suggestion> allTokensFrom(String parent, TrieNode trieNode) {
        List<Suggestion> results = new ArrayList<>();
        recurse(parent, trieNode, results);
        return results;
    }

    private void recurse(String parent, TrieNode trieNode, List<Suggestion> results) {
        if (trieNode.getChildren().isEmpty()) {
            // End of token
            Suggestion suggestion = new Suggestion(parent, trieNode.getType(), trieNode.getTypeName());
            results.add(suggestion);
            return;
        }
        trieNode.getChildren().forEach((character, trieNode1) -> {
            if (character != '.') {
                String newValue = parent + character;
                recurse(newValue, trieNode1, results);
            } else {
                Suggestion suggestion = new Suggestion(parent, trieNode.getType(), trieNode.getTypeName());
                results.add(suggestion);
            }
        });
    }
}
