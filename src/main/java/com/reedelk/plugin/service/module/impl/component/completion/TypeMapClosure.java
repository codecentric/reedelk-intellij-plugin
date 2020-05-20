package com.reedelk.plugin.service.module.impl.component.completion;

public class TypeMapClosure implements TypeProxy {

    private final TypeProxy typeProxy;

    public TypeMapClosure(TypeProxy typeProxy) {
        this.typeProxy = typeProxy;
    }

    @Override
    public boolean isList(TypeAndTries typeAndTries) {
        return false;
    }

    @Override
    public Trie resolve(TypeAndTries typeAndTries) {
        return new ClosureTrie();
    }

    @Override
    public String toSimpleName(TypeAndTries typeAndTries) {
        return "";
    }

    @Override
    public String getTypeFullyQualifiedName() {
        return "";
    }

    @Override
    public String listItemType(TypeAndTries typeAndTries) {
        return null;
    }

    class ClosureTrie extends TrieDefault {

        public ClosureTrie() {
            insert(Suggestion.create(Suggestion.Type.FUNCTION)
                    .insertValue("{")
                    .returnType(typeProxy) // this is the specific return type
                    .build());
        }
    }
}
