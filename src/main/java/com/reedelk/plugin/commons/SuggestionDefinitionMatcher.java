package com.reedelk.plugin.commons;

import com.reedelk.plugin.service.module.impl.completion.SuggestionType;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SuggestionDefinitionMatcher {

    private static final Pattern MATCH_SUGGESTION = Pattern.compile("(.*)\\[(.*):(.*)]$");

    public static Optional<Triple<String, SuggestionType,String>> of(String suggestionTokenDefinition) {
        Matcher matcher = MATCH_SUGGESTION.matcher(suggestionTokenDefinition);
        if (matcher.matches()) {
            String tokenString = matcher.group(1);
            SuggestionType type = SuggestionType.valueOf(matcher.group(2));
            String typeName = matcher.group(3);
            return Optional.of(new ImmutableTriple<>(tokenString, type, typeName));
        } else {
            return Optional.empty();
        }
    }
}
