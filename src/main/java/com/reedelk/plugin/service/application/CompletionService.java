package com.reedelk.plugin.service.application;

import com.intellij.openapi.components.ServiceManager;
import com.reedelk.plugin.service.application.impl.completion.Suggestion;
import com.reedelk.plugin.service.application.impl.completion.SuggestionType;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface CompletionService {
    static CompletionService getInstance() {
        return ServiceManager.getService(CompletionService.class);
    }

    Optional<List<Suggestion>> completionTokensOf(String token);

    Pattern MATCH_SUGGESTION = Pattern.compile("(.*)\\[(.*):(.*)]$");

    static Optional<Triple<String, SuggestionType,String>> parseSuggestionToken(String suggestionTokenDefinition) {
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
