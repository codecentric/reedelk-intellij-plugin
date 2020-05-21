package com.reedelk.plugin.service.module.impl.component.completion;

import java.util.Collection;

import static com.reedelk.plugin.service.module.impl.component.completion.Suggestion.Type.FUNCTION;
import static java.util.Collections.singletonList;

public class ClosureAware {

    private ClosureAware() {

    }

    public static class TypeClosureAware extends TypeProxyDefault {

        // List or Map type proxy
        private final TypeProxy typeProxy;

        TypeClosureAware(String typeFullyQualifiedName, TypeProxy typeProxy) {
            super(typeFullyQualifiedName);
            this.typeProxy = typeProxy;
        }

        @Override
        public Trie resolve(TypeAndTries typeAndTries) {
            Trie originalTypeTrie = typeAndTries.getOrDefault(getTypeFullyQualifiedName());
            return new ClosureAware.TrieClosureAware(originalTypeTrie, typeProxy);
        }

        @Override
        public boolean equals(Object o) {
            return super.equals(o);
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }
    }

    private static class TrieClosureAware extends TrieDefault {

        private final Trie trieDefault;
        private final TypeProxy closureType;

        public TrieClosureAware(Trie trieDefault, TypeProxy closureType) {
            this.trieDefault = trieDefault;
            this.closureType = closureType;
        }

        @Override
        public Collection<Suggestion> autocomplete(String word, TypeAndTries typeAndTrieMap) {
            if (word.equals("{")) {
                // beginning of a closure
                Suggestion build = Suggestion.create(FUNCTION)
                        .insertValue("{")
                        .returnType(closureType)
                        .build();
                return singletonList(SuggestionFactory.copyWithType(typeAndTrieMap, build, closureType));
            } else {
                return trieDefault.autocomplete(word, typeAndTrieMap);
            }
        }
    }
}
