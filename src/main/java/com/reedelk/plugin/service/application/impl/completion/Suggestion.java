package com.reedelk.plugin.service.application.impl.completion;

public class Suggestion {

    private final String token;
    private final String typeName;
    private SuggestionType type;

    public Suggestion(String token, SuggestionType type, String typeName) {
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
}
