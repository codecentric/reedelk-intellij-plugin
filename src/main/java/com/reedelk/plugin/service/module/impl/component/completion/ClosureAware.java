package com.reedelk.plugin.service.module.impl.component.completion;

import java.util.Collection;

import static com.reedelk.plugin.service.module.impl.component.completion.Suggestion.Type.FUNCTION;
import static java.util.Collections.singletonList;

public class ClosureAware {

    private static final String BEGIN_CLOSURE_SYMBOL = "{";

    private ClosureAware() {
    }

    public interface KeepReturnType {
    }

    public static class TypeClosureAware extends TypeProxyDefault {

        // List or Map type proxy
        private final TypeProxy closureReturnType;

        TypeClosureAware(String typeFullyQualifiedName, TypeProxy closureReturnType) {
            super(typeFullyQualifiedName);
            this.closureReturnType = closureReturnType;
        }

        @Override
        public Trie resolve(TypeAndTries typeAndTries) {
            Trie originalTypeTrie = typeAndTries.getOrDefault(getTypeFullyQualifiedName());
            // Resolve to a dynamic trie returning the beginning of a closure or the original type
            // suggestions if the token is different from '{'. This is needed to continuously
            // be able to resolve types when a closure is closed, e.g map.each { entry.name }.myMethod
            return new TrieClosureAware(typeFullyQualifiedName, originalTypeTrie, closureReturnType);
        }
    }

    private static class TrieClosureAware extends TrieDefault {

        private final Trie originalTypeTrie;
        private final TypeProxy closureReturnType;

        public TrieClosureAware(String fullyQualifiedName, Trie originalTypeTrie, TypeProxy closureReturnType) {
            super(fullyQualifiedName);
            this.originalTypeTrie = originalTypeTrie;
            this.closureReturnType = closureReturnType;
        }

        @Override
        public Collection<Suggestion> autocomplete(String token, TypeAndTries typeAndTrieMap) {
            if (token.equals(BEGIN_CLOSURE_SYMBOL)) {
                // beginning of a closure
                Suggestion closureSymbolSuggestion = Suggestion.create(FUNCTION)
                        .insertValue(BEGIN_CLOSURE_SYMBOL)
                        .returnType(closureReturnType)
                        .build();
                return singletonList(closureSymbolSuggestion);
            } else {
                return originalTypeTrie.autocomplete(token, typeAndTrieMap);
            }
        }

        // The simple name is taken from the original type trie.
        @Override
        public String toSimpleName(TypeAndTries typeAndTries) {
            return originalTypeTrie.toSimpleName(typeAndTries);
        }
    }
}
