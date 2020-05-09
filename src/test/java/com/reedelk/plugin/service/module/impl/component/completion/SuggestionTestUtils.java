package com.reedelk.plugin.service.module.impl.component.completion;

public class SuggestionTestUtils {

    public static Suggestion createFunction(String lookup, String name, String type) {
        return Suggestion.create(Suggestion.Type.FUNCTION)
                .withLookupString(lookup)
                .withName(name)
                .withType(type)
                .build();
    }

    public static Suggestion createProperty(String lookup, String name, String type) {
        return Suggestion.create(Suggestion.Type.PROPERTY)
                .withLookupString(lookup)
                .withName(name)
                .withType(type)
                .build();
    }
}
