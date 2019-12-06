package com.reedelk.plugin.service.module;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleServiceManager;
import com.reedelk.plugin.service.module.impl.completion.Suggestion;
import com.reedelk.plugin.service.module.impl.completion.SuggestionType;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface CompletionService {

    static CompletionService getInstance(@NotNull Module module) {
        return ModuleServiceManager.getService(module, CompletionService.class);
    }

    Optional<List<Suggestion>> completionTokensOf(String token);


    // TODO: Extract  this matcher into  its own function.
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
