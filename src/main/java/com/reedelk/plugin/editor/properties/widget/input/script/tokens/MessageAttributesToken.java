package com.reedelk.plugin.editor.properties.widget.input.script.tokens;

import java.util.Arrays;
import java.util.Collection;

public class MessageAttributesToken implements Token {

    @Override
    public String base() {
        return "attributes()";
    }

    @Override
    public Collection<Token> children() {
        return Arrays.asList(
                new LeafToken("get('key')"),
                new LeafToken("contains('key')"));
    }
}
