package com.reedelk.plugin.completion.token;

public class LeafToken implements Token {

    private final String base;

    public LeafToken(String base) {
        this.base = base;
    }

    @Override
    public String base() {
        return base;
    }
}
