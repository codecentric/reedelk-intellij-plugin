package com.reedelk.plugin.editor.properties.widget.input.script.tokens;

import java.util.Arrays;
import java.util.Collection;

// TODO: Complete me
public class ContextToken implements Token {
    @Override
    public String base() {
        return "context";
    }

    @Override
    public Collection<Token> children() {
        return Arrays.asList(new LeafToken("put()"));
    }
}
