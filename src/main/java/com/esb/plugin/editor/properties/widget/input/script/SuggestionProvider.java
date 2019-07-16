package com.esb.plugin.editor.properties.widget.input.script;

import org.jetbrains.annotations.NotNull;

import java.util.Set;

public interface SuggestionProvider {

    @NotNull
    Set<Suggestion> suggest(String text);
}
