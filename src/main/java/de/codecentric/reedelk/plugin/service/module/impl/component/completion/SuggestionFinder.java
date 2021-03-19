package de.codecentric.reedelk.plugin.service.module.impl.component.completion;

import de.codecentric.reedelk.plugin.service.module.impl.component.metadata.PreviousComponentOutput;

import java.util.Collection;

public interface SuggestionFinder {

    Collection<Suggestion> suggest(Trie root, PreviousComponentOutput previousOutput);

    Collection<Suggestion> suggest(Trie root, String[] tokens, PreviousComponentOutput previousOutput);

}
