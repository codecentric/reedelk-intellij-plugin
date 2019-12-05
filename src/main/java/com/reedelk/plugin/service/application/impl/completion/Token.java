package com.reedelk.plugin.service.application.impl.completion;

import java.util.Collection;
import java.util.HashSet;

public interface Token {

    String base();

    default Collection<Token> children() {
        return new HashSet<>();
    }

}
