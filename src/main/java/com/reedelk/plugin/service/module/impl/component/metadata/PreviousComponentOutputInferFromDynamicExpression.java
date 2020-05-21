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

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

public class PreviousComponentOutputInferFromDynamicExpression extends AbstractPreviousComponentOutput {

    private final PreviousComponentOutput previousOutput;
    private final String dynamicExpression;

    public PreviousComponentOutputInferFromDynamicExpression(PreviousComponentOutput previousOutput, String dynamicExpression) {
        this.previousOutput = previousOutput;
        this.dynamicExpression = dynamicExpression;
    }

    @Override
    public Collection<Suggestion> buildDynamicSuggestions(SuggestionFinder suggestionFinder, Suggestion suggestion, TypeAndTries typeAndTrieMap, boolean flatten) {
        Collection<Suggestion> dynamicSuggestions = suggestionsFromDynamicExpression(suggestionFinder)
                .stream()
                .map(dynamicType -> Suggestion.create(suggestion.getType())
                        .cursorOffset(suggestion.getCursorOffset())
                        .insertValue(suggestion.getInsertValue())
                        .lookupToken(suggestion.getLookupToken())
                        .tailText(suggestion.getTailText())
                        .returnTypeDisplayValue(dynamicType.getReturnType().toSimpleName(typeAndTrieMap))
                        .returnType(dynamicType.getReturnType())
                        .build())
                .collect(toList());

        return flatten ?
                singletonList(flatten(dynamicSuggestions, typeAndTrieMap)) :
                dynamicSuggestions;
    }

    @Override
    public String description() {
        return StringUtils.EMPTY;
    }

    @Override
    public MetadataTypeDTO mapAttributes(SuggestionFinder suggestionFinder, TypeAndTries typeAndTries) {
        return previousOutput.mapAttributes(suggestionFinder, typeAndTries);
    }

    @Override
    public List<MetadataTypeDTO> mapPayload(SuggestionFinder suggestionFinder, TypeAndTries typeAndTries) {
        Collection<Suggestion> suggestions = suggestionsFromDynamicExpression(suggestionFinder);
        return suggestions
                .stream()
                .map(Suggestion::getReturnType)
                .map(typeProxy -> createMetadataType(suggestionFinder, typeAndTries, typeProxy))
                .collect(toList());
    }

    @NotNull
    private Collection<Suggestion> suggestionsFromDynamicExpression(SuggestionFinder suggestionFinder) {
        String unwrap = ScriptUtils.unwrap(dynamicExpression);
        String[] tokens = Tokenizer.tokenize(unwrap, unwrap.length());
        return suggestionFinder.suggest(TypeDefault.MESSAGE_AND_CONTEXT, tokens, previousOutput);
    }
}
