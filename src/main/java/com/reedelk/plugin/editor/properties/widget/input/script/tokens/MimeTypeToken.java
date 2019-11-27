package com.reedelk.plugin.editor.properties.widget.input.script.tokens;

import java.util.Collection;

public class MimeTypeToken implements Token {

    @Override
    public String base() {
        return "mimeType()";
    }

    @Override
    public Collection<Token> children() {
        return null;
    }
}
