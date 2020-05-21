package com.reedelk.plugin.service.module.impl.component.completion;

import java.util.Collection;

import static com.reedelk.plugin.service.module.impl.component.completion.Suggestion.Type.FUNCTION;
import static java.util.Collections.singletonList;

public class ClosureAware {

    private static final String BEGIN_CLOSURE_SYMBOL = "{";

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
            // Resolve to a dynamic trie returning the beginning of a closure or the original type
            // suggestions if the token is different from '{'. This is needed to continuously
            // be able to resolve types when a closure is closed, e.g map.each { entry.name }.myMethod
            return new TrieDefault() {
                @Override
                public Collection<Suggestion> autocomplete(String word, TypeAndTries typeAndTrieMap) {
                    if (word.equals(BEGIN_CLOSURE_SYMBOL)) {
                        // beginning of a closure
                        Suggestion build = Suggestion.create(FUNCTION)
                                .returnType(typeProxy)
                                .insertValue(BEGIN_CLOSURE_SYMBOL)
                                .build();
                        return singletonList(SuggestionFactory.copyWithType(typeAndTrieMap, build, typeProxy));
                    } else {
                        return originalTypeTrie.autocomplete(word, typeAndTrieMap);
                    }
                }
            };
        }
    }
}
