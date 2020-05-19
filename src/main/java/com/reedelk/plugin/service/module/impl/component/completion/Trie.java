package com.reedelk.plugin.service.module.impl.component.completion;

import java.util.Collection;

public interface Trie {

    String displayName();

    String extendsType();

    Collection<Suggestion> autocomplete(String word, TypeAndTries typeAndTrieMap);

    void clear();

    void insert(Suggestion suggestion);

    // TODO: DO I need this? Is there a way to encapsulate it?
    default String listItemType() {
        return null;
    }
}
