package com.reedelk.plugin.editor.properties.widget.input.script.tokens;

import java.util.Collection;

import static java.util.Collections.singletonList;

public class PayloadToken implements Token {

    @Override
    public String base() {
        return "payload()";
    }

    @Override
    public Collection<Token> children() {
        return singletonList(new LeafToken("toString()"));
    }
}
