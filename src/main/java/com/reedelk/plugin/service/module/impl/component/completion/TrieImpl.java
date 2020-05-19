package com.reedelk.plugin.service.module.impl.component.completion;

import com.reedelk.runtime.api.commons.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class TrieImpl implements Trie {

    private final String extendsType;
    private final String displayName;
    private final String listItemType;
    private final String mapKeyType;
    private final String mapValueType;
    private TrieNode root;

    public TrieImpl() {
        this.root = new TrieNode();
        this.displayName = null;
        this.extendsType = null;
        this.mapKeyType = null;
        this.mapValueType = null;
        this.listItemType = null;
    }

    public TrieImpl(String extendsType, String listItemType, String displayName, String mapKeyType, String mapValueType) {
        this.root = new TrieNode();
        this.displayName = displayName;
        this.extendsType = extendsType;
        this.listItemType = listItemType;
        this.mapKeyType = mapKeyType;
        this.mapValueType = mapValueType;
    }

    @Override
    public void insert(Suggestion suggestion) {
        TrieNode current = root;
        String word = suggestion.getInsertValue();
        for (int i = 0; i < word.length(); i++) {
            current = current.getChildren().computeIfAbsent(word.charAt(i), c -> new TrieNode());
        }
        current.addSuggestion(suggestion);
    }

    @Override
    public Collection<Suggestion> autocomplete(String word, TypeAndTries typeAndTrieMap) {
        Set<Suggestion> autocomplete = autocomplete(word);
        List<Suggestion> suggestions = addExtendsTypeSuggestions(this, typeAndTrieMap, word);
        autocomplete.addAll(suggestions);
        return autocomplete.stream().map(suggestion -> {
            if (TypeClosure.class.getName().equals(suggestion.getReturnType().getTypeFullyQualifiedName())) {
                if (listItemType != null) {
                    TypeProxyClosureList listAwareProxy = new TypeProxyClosureList(listItemType);
                    return Suggestion.create(Suggestion.Type.PROPERTY)
                            .tailText(suggestion.getTailText())
                            .cursorOffset(suggestion.getCursorOffset())
                            .lookupToken(suggestion.getLookupToken())
                            .insertValue(suggestion.getInsertValue())
                            .returnType(listAwareProxy)
                            .returnTypeDisplayValue(listAwareProxy.toSimpleName(typeAndTrieMap))
                            .build();
                } else if (mapValueType != null) {
                    TypeProxyClosureMap mapAwareProxy = new TypeProxyClosureMap(mapKeyType, mapValueType);
                    return Suggestion.create(Suggestion.Type.PROPERTY)
                            .tailText(suggestion.getTailText())
                            .cursorOffset(suggestion.getCursorOffset())
                            .lookupToken(suggestion.getLookupToken())
                            .insertValue(suggestion.getInsertValue())
                            .returnType(mapAwareProxy)
                            .returnTypeDisplayValue(mapAwareProxy.toSimpleName(typeAndTrieMap))
                            .build();
                }
            }
            return suggestion;
        }).collect(Collectors.toList());
    }

    @Override
    public String extendsType() {
        return extendsType;
    }

    @Override
    public String listItemType() {
        return listItemType;
    }

    @Override
    public String displayName() {
        return displayName;
    }

    @Override
    public void clear() {
        this.root = null;
        this.root = new TrieNode();
    }

    private Set<Suggestion> autocomplete(String token) {
        Set<Suggestion> suggestions = new HashSet<>();
        TrieNode current = root;
        int i = 0;
        while (i < token.length()) {
            char c = token.charAt(i);
            Map<Character, TrieNode> children = current.getChildren();
            if (children.containsKey(c)) {
                current = children.get(c);
            } else {
                return suggestions;
            }
            i++;
        }

        if (current.existAnySuggestion()) {
            return new HashSet<>(current.getSuggestions());
        } else {
            // We need to complete the suggestion from the current token
            // string until some suggestions are found.
            return traversal(current, suggestions, token);
        }
    }

    private Set<Suggestion> traversal(TrieNode start, Set<Suggestion> suggestions, String current) {
        if (start.existAnySuggestion()) {
            // We add the suggestions from the current node to the list of suggestions
            // to return and then we continue with the children to discover other suggestions
            // matching the postfix below.
            suggestions.addAll(start.getSuggestions());
        }
        // We must continue to find suggestions until there are no more children in the trie.
        start.getChildren().forEach((character, trieNode) -> {
            String newCurrent = current + character;
            traversal(trieNode, suggestions, newCurrent);
        });
        return suggestions;
    }

    // All the extends type must contain the original
    private List<Suggestion> addExtendsTypeSuggestions(Trie current, TypeAndTries typeAndTrieMap, String token) {
        // TODO: Check on default object, otherwise we would go on stackoverflow (the type object check should not be needed, it was needed because I used it as extends type!!)
        List<Suggestion> suggestions = new ArrayList<>();
        if (current != TypeDefault.OBJECT && current != null && StringUtils.isNotBlank(current.extendsType())) {
            String extendsType = current.extendsType();
            Trie currentTypeTrie = typeAndTrieMap.getOrDefault(extendsType);
            Collection<Suggestion> autocomplete = currentTypeTrie.autocomplete(token, typeAndTrieMap);
            suggestions.addAll(autocomplete);
            List<Suggestion> suggestions1 = addExtendsTypeSuggestions(currentTypeTrie, typeAndTrieMap, token);
            suggestions.addAll(suggestions1);
        }
        return suggestions;
    }
}
