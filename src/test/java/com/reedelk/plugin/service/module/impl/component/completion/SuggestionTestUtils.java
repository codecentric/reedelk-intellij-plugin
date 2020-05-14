package com.reedelk.plugin.service.module.impl.component.completion;

public class SuggestionTestUtils {

    public static Suggestion createFunction(String lookup, String returnType) {
        return Suggestion.create(Suggestion.Type.FUNCTION)
                .returnType(returnType)
                .lookup(lookup)
                .build();
    }

    public static Suggestion createProperty(String lookup, String returnType) {
        return Suggestion.create(Suggestion.Type.PROPERTY)
                .returnType(returnType)
                .lookup(lookup)
                .build();
    }
}
