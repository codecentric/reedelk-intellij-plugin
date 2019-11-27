package com.reedelk.plugin.editor.properties.widget.input.script.tokens;

import java.util.Arrays;
import java.util.Collection;

public class TypedContentToken implements Token {

    @Override
    public String base() {
        return "content()";
    }

    @Override
    public Collection<Token> children() {
        return Arrays.asList(
                new LeafToken("type()"),
                new MimeTypeToken(),
                new LeafToken("data()"),
                new TypedPublisherToken(),
                new LeafToken("isStream()"),
                new LeafToken("isConsumed()"),
                new LeafToken("consume()"));
    }
}
