package com.reedelk.plugin.editor.properties.widget.input.script.tokens;

import java.util.Collection;
import java.util.HashSet;

public interface Token {

    String base();

    default Collection<Token> children() {
        return new HashSet<>();
    }

}
