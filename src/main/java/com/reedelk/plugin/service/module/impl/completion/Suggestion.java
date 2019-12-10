package com.reedelk.plugin.service.module.impl.completion;

import java.util.Objects;

public class Suggestion {

    private final String token;
    private final String typeName;
    private SuggestionType type;

    public static Suggestion from(String token, SuggestionType type, String typeName) {
        return new Suggestion(token, type, typeName);
    }

    private Suggestion(String token, SuggestionType type, String typeName) {
        this.token = token;
        this.type = type == null ? SuggestionType.UNKNOWN : type;
        this.typeName = typeName;
    }

    public String getToken() {
        return token;
    }

    public SuggestionType getType() {
        return type;
    }

    public String getTypeName() {
        return typeName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Suggestion that = (Suggestion) o;
        return token.equals(that.token) &&
                typeName.equals(that.typeName) &&
                type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(token, typeName, type);
    }
}
