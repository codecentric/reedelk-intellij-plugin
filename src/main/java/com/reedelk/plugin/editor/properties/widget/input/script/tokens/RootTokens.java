package com.reedelk.plugin.editor.properties.widget.input.script.tokens;

import java.util.Arrays;
import java.util.Collection;

public class RootTokens implements Token {

    @Override
    public String base() {
        return "";
    }

    @Override
    public Collection<Token> children() {
        return Arrays.asList(new MessageToken(), new ContextToken());
    }
}
