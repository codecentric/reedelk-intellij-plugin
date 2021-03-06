package de.codecentric.reedelk.plugin.service.module.impl.component.metadata;

import de.codecentric.reedelk.plugin.completion.Tokenizer;
import de.codecentric.reedelk.plugin.service.module.impl.component.completion.*;
import de.codecentric.reedelk.runtime.api.commons.ScriptUtils;
import de.codecentric.reedelk.runtime.api.commons.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

public class PreviousComponentOutputInferFromDynamicExpression extends AbstractPreviousComponentOutput {

    private final PreviousComponentOutput previousOutput;
    private final String dynamicExpression;

    public PreviousComponentOutputInferFromDynamicExpression(PreviousComponentOutput previousOutput, String dynamicExpression) {
        this.previousOutput = previousOutput;
        this.dynamicExpression = dynamicExpression;
    }

    @Override
    public Collection<Suggestion> buildDynamicSuggestions(@NotNull SuggestionFinder suggester,
                                                          @NotNull Suggestion suggestion,
                                                          @NotNull TypeAndTries typeAndTrieMap) {
        return suggestionsFromDynamicExpression(suggester)
                .stream()
                .map(dynamicType -> Suggestion.create(suggestion.getType())
                        .returnTypeDisplayValue(dynamicType.getReturnType().toSimpleName(typeAndTrieMap))
                        .cursorOffset(suggestion.getCursorOffset())
                        .insertValue(suggestion.getInsertValue())
                        .lookupToken(suggestion.getLookupToken())
                        .returnType(dynamicType.getReturnType())
                        .tailText(suggestion.getTailText())
                        .build())
                .collect(toList());
    }

    @Override
    public String description() {
        return StringUtils.EMPTY;
    }

    @Override
    public MetadataTypeDTO mapAttributes(@NotNull SuggestionFinder suggester, @NotNull TypeAndTries typeAndTries) {
        return previousOutput.mapAttributes(suggester, typeAndTries);
    }

    @Override
    public List<MetadataTypeDTO> mapPayload(@NotNull SuggestionFinder suggester, @NotNull TypeAndTries typeAndTries) {
        Collection<Suggestion> suggestions = suggestionsFromDynamicExpression(suggester);
        // Because the suggestions might be each and eachWithIndex both returning list.
        // therefore they have the same return type and we must merge them.
        Map<String, List<Suggestion>> suggestionsByFullyQualifiedName =
                suggestions.stream().collect(groupingBy(suggestion -> suggestion.getReturnType().getTypeFullyQualifiedName()));
        return suggestionsByFullyQualifiedName
                .values()
                .stream()
                .map(suggestionList -> {
                    Suggestion next = suggestionList.iterator().next();
                    return createMetadataType(suggester, typeAndTries, next.getReturnType());
                }).collect(toList());
    }

    @NotNull
    private Collection<Suggestion> suggestionsFromDynamicExpression(SuggestionFinder suggester) {
        String unwrap = ScriptUtils.unwrap(dynamicExpression);
        if (StringUtils.isBlank(unwrap) || unwrap.endsWith(".")) {
            // If the dynamic value is blank or ends with "." (e.g message.) we cannot infer correctly
            // the output and we return the default suggestion.
            return defaultSuggestion();
        }

        String[] tokens = Tokenizer.tokenize(unwrap, unwrap.length());
        Collection<Suggestion> suggestions = suggester.suggest(TypeBuiltIn.MESSAGE_AND_CONTEXT, tokens, previousOutput);
        if (!suggestions.isEmpty()) return suggestions;

        // If there are no suggestions, it means that we could not evaluate the expression,
        // for example because it is just a literal or a sum operation or null, therefore we return
        // a generic object suggestion.
        return defaultSuggestion();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PreviousComponentOutputInferFromDynamicExpression that = (PreviousComponentOutputInferFromDynamicExpression) o;
        return Objects.equals(previousOutput, that.previousOutput) &&
                Objects.equals(dynamicExpression, that.dynamicExpression);
    }

    @Override
    public int hashCode() {
        return Objects.hash(previousOutput, dynamicExpression);
    }

    @Override
    public String toString() {
        return "PreviousComponentOutputInferFromDynamicExpression{" +
                "previousOutput=" + previousOutput +
                ", dynamicExpression='" + dynamicExpression + '\'' +
                '}';
    }

    // The default suggestion when the output could not be determined from the dynamic expression.
    @NotNull
    private Collection<Suggestion> defaultSuggestion() {
        return Collections.singletonList(Suggestion.create(Suggestion.Type.PROPERTY)
                .insertValue(StringUtils.EMPTY)
                .returnType(TypeProxy.create(Object.class))
                .build());
    }
}
