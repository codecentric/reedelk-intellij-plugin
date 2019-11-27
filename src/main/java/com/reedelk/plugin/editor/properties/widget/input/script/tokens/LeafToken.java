package com.reedelk.plugin.editor.properties.widget.input.script.tokens;

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
