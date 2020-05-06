package com.reedelk.plugin.service.module.impl.component.completion;

public class TypeInfo {

    private String extendsType;
    private TrieDefault trie;

    public TypeInfo() {

    }

    public TypeInfo(String extendsType, TrieDefault trie) {
        this.extendsType = extendsType;
        this.trie = trie;
    }

    public String getExtendsType() {
        return extendsType;
    }

    public TrieDefault getTrie() {
        return trie;
    }
}
