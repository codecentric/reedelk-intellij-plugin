package com.reedelk.plugin.service.module.impl.component.metadata;

import com.reedelk.plugin.completion.Tokenizer;
import com.reedelk.plugin.service.module.impl.component.completion.Suggestion;
import com.reedelk.plugin.service.module.impl.component.completion.SuggestionFinder;
import com.reedelk.plugin.service.module.impl.component.completion.TypeAndTries;
import com.reedelk.plugin.service.module.impl.component.completion.TypeDefault;
import com.reedelk.runtime.api.commons.ScriptUtils;
import com.reedelk.runtime.api.commons.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

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
        return suggestions
                .stream()
                .map(Suggestion::getReturnType)
                .map(typeProxy -> createMetadataType(suggester, typeAndTries, typeProxy))
                .collect(toList());
    }

    @NotNull
    private Collection<Suggestion> suggestionsFromDynamicExpression(SuggestionFinder suggester) {
        String unwrap = ScriptUtils.unwrap(dynamicExpression);
        String[] tokens = Tokenizer.tokenize(unwrap, unwrap.length());
        return suggester.suggest(TypeDefault.MESSAGE_AND_CONTEXT, tokens, previousOutput);
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
}
