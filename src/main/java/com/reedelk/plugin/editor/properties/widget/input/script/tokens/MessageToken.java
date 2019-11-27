package com.reedelk.plugin.editor.properties.widget.input.script.tokens;

import java.util.Arrays;
import java.util.Collection;

public class MessageToken implements Token {

    @Override
    public String base() {
        return "message";
    }

    @Override
    public Collection<Token> children() {
        return Arrays.asList(
                new TypedContentToken(),
                new PayloadToken(),
                new MessageAttributesToken(),
                new LeafToken("toString()"));
    }
}
