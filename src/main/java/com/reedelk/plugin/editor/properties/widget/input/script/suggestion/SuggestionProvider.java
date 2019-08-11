package com.reedelk.plugin.editor.properties.widget.input.script.suggestion;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface SuggestionProvider {

    @NotNull
    List<Suggestion> suggest(String text);
}
