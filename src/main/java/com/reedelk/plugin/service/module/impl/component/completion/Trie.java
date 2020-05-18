package com.reedelk.plugin.service.module.impl.component.completion;

import java.util.Collection;

public interface Trie {

    String displayName();

    String extendsType();

    String listItemType();

    Collection<Suggestion> autocomplete(String word, TypeAndTries typeAndTrieMap);

    void clear();

    void insert(Suggestion suggestion);
}
