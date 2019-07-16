package com.esb.plugin.editor.properties.widget.input.script;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Suggestion that = (Suggestion) o;
        return suggestionType == that.suggestionType &&
                token.equals(that.token);
    }

    @Override
    public int hashCode() {
        return Objects.hash(suggestionType, token);
    }
}
