package com.reedelk.plugin.service.application.impl.completion;

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
