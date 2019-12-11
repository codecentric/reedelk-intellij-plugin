package com.reedelk.plugin.service.module.impl.completion;

import java.util.Objects;
import java.util.Optional;

public class Suggestion {

    private final String token;
    private final String typeName;
    private final Integer offset;
    private SuggestionType type;

    public static Suggestion from(String token, SuggestionType type, String typeName, Integer offset) {
        return new Suggestion(token, type, typeName, offset);
    }

    public static Suggestion from(String token, Suggestion suggestion) {
        return new Suggestion(token, suggestion.type, suggestion.typeName, suggestion.offset);
    }

    private Suggestion(String token, SuggestionType type, String typeName, Integer offset) {
        this.token = token;
        this.type = type == null ? SuggestionType.UNKNOWN : type;
        this.typeName = typeName;
        this.offset = offset;
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

    public Optional<Integer> getOffset() {
        return Optional.ofNullable(offset);
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

    @Override
    public String toString() {
        return "Suggestion{" +
                "token='" + token + '\'' +
                ", typeName='" + typeName + '\'' +
                ", type=" + type +
                '}';
    }
}
