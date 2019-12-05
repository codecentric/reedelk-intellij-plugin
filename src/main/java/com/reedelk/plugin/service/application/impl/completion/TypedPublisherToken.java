package com.reedelk.plugin.service.application.impl.completion;

import java.util.Collection;
import java.util.Collections;

public class TypedPublisherToken implements Token {

    @Override
    public String base() {
        return "stream()";
    }

    @Override
    public Collection<Token> children() {
        return Collections.singletonList(new LeafToken("getType()"));
    }
}
