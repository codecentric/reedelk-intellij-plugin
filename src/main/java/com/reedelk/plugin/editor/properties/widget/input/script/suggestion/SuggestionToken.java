package com.reedelk.plugin.editor.properties.widget.input.script.suggestion;

public class SuggestionToken {
    public String value;
    public SuggestionType type;

    public SuggestionToken(String value, SuggestionType type) {
        this.value = value;
        this.type = type;
    }
}
