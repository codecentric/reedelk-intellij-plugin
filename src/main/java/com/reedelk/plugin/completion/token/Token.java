package com.reedelk.plugin.completion.token;

import java.util.Collection;
import java.util.HashSet;

public interface Token {

    String base();

    default Collection<Token> children() {
        return new HashSet<>();
    }

}
