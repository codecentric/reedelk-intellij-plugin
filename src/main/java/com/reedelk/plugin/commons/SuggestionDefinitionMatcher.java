package com.reedelk.plugin.commons;

import com.reedelk.plugin.service.module.impl.completion.Suggestion;
import com.reedelk.plugin.service.module.impl.completion.SuggestionType;
import com.reedelk.runtime.api.commons.StringUtils;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SuggestionDefinitionMatcher {

    private static final Pattern MATCH_SUGGESTION = Pattern.compile("(.*)\\[([a-zA-Z]*):([^:]*):?(.*)?]$");

    private SuggestionDefinitionMatcher() {
    }

    public static Optional<Suggestion> of(String suggestionTokenDefinition) {
        Matcher matcher = MATCH_SUGGESTION.matcher(suggestionTokenDefinition);
        if (!matcher.matches()) return Optional.empty();

        String token = matcher.group(1);
        SuggestionType type = SuggestionType.valueOf(matcher.group(2));
        String typeName = matcher.group(3);
        Integer offset = parseIntOrNull(matcher.group(4));
        Suggestion specification = Suggestion.from(token, type, typeName, offset);
        return Optional.of(specification);
    }

    private static Integer parseIntOrNull(String possibleInt) {
        if (StringUtils.isBlank(possibleInt)) return null;
        try {
            return Integer.parseInt(possibleInt);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
