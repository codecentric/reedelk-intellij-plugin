package com.reedelk.plugin.completion.token;

import java.util.Arrays;
import java.util.Collection;

public class MimeTypeToken implements Token {

    @Override
    public String base() {
        return "mimeType()";
    }

    @Override
    public Collection<Token> children() {
        return Arrays.asList(
                new LeafToken("getSubType()"),
                new LeafToken("getCharset()"),
                new LeafToken("getPrimaryType()"),
                new LeafToken("getFileExtensions()"));
    }
}
