package com.esb.plugin.editor.properties.widget.input.script;

public class Suggestion {

    private final SuggestionType suggestionType;
    private final String token;

    public Suggestion(SuggestionType suggestionType, String token) {
        this.suggestionType = suggestionType;
        this.token = token;
    }

    public SuggestionType getSuggestionType() {
        return suggestionType;
    }

    public String getToken() {
        return token;
    }
}
