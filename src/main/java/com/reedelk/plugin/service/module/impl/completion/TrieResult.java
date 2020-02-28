package com.reedelk.plugin.service.module.impl.completion;

public class TrieResult<T extends TypeAware> {

    private String word;
    private T typeAware;

    TrieResult(String word, T typeAware) {
        this.word = word;
        this.typeAware = typeAware;
    }

    public String getWord() {
        return word;
    }

    public T getTypeAware() {
        return typeAware;
    }
}
