package com.reedelk.plugin.editor.properties.widget.input.script.suggestion;

public class SuggestionToken {
    
    public final String value;
    public final SuggestionType type;

    SuggestionToken(String value, SuggestionType type) {
        this.value = value;
        this.type = type;
    }
}
