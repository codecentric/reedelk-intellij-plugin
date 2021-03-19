package de.codecentric.reedelk.plugin.service.module.impl.component.completion;

import java.util.Collection;

public interface Trie {

    String toSimpleName(TypeAndTries typeAndTries);

    void clear();

    void insert(Suggestion suggestion);

    Collection<Suggestion> autocomplete(String word, TypeAndTries typeAndTrieMap);

    default Collection<Suggestion> autocompleteForExtend(String word, TypeAndTries typeAndTrieMap) {
        return autocomplete(word, typeAndTrieMap);
    }

    default boolean isList() {
        return false;
    }

    default boolean isMap() {
        return false;
    }

    default TypeProxy listItemType(TypeAndTries typeAndTries) {
        throw new UnsupportedOperationException();
    }

    default TypeProxy mapKeyType(TypeAndTries typeAndTries) {
        throw new UnsupportedOperationException();
    }

    default TypeProxy mapValueType(TypeAndTries typeAndTries) {
        throw new UnsupportedOperationException();
    }
}
