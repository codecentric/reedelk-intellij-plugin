package com.reedelk.plugin.service.module.impl.completion;

public class TypeInfo {

    private String extendsType;
    private Trie trie;

    public TypeInfo() {

    }

    public TypeInfo(String extendsType, Trie trie) {
        this.extendsType = extendsType;
        this.trie = trie;
    }

    public String getExtendsType() {
        return extendsType;
    }

    public Trie getTrie() {
        return trie;
    }
}
