package com.esb.plugin.editor.properties.widget.input.script;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface SuggestionProvider {

    @NotNull
    List<Suggestion> suggest(String text);
}
