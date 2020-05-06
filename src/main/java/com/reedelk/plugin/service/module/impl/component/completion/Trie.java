package com.reedelk.plugin.service.module.impl.component.completion;

import java.util.Collection;

public interface Trie {

    void clear();

    void insert(Suggestion suggestion);

    Collection<Suggestion> autocomplete(String word);
}
