package com.reedelk.plugin.service.application.impl.completion;

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
