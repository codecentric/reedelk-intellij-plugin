package com.reedelk.plugin.service.module.impl.component.completion;

import java.util.Collection;

public interface Trie {

    String extendsType();

    String listItemType();

    String displayName();

    String fullyQualifiedName();

    void clear();

    void insert(Suggestion suggestion);

    Collection<Suggestion> autocomplete(String word, TypeAndTries typeAndTrieMap);
}
