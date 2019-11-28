package com.reedelk.plugin.completion.token;

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
