package com.reedelk.plugin.service.module.impl.component.metadata;

import com.reedelk.plugin.completion.TokenFinder;
import com.reedelk.plugin.service.module.impl.component.completion.CompletionFinder;
import com.reedelk.plugin.service.module.impl.component.completion.Suggestion;
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
    public Collection<Suggestion> buildDynamicSuggestions(CompletionFinder completionFinder, Suggestion suggestion, TypeAndTries typeAndTrieMap, boolean flatten) {
        Collection<Suggestion> dynamicSuggestions = suggestionsFromDynamicExpression(completionFinder)
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
    public MetadataTypeDTO mapAttributes(CompletionFinder completionFinder, TypeAndTries typeAndTries) {
        return previousOutput.mapAttributes(completionFinder, typeAndTries);
    }

    @Override
    public List<MetadataTypeDTO> mapPayload(CompletionFinder completionFinder, TypeAndTries typeAndTries) {
        Collection<Suggestion> suggestions = suggestionsFromDynamicExpression(completionFinder);
        return suggestions
                .stream()
                .map(Suggestion::getReturnType)
                .map(typeProxy -> createMetadataType(completionFinder, typeAndTries, typeProxy))
                .collect(toList());
    }

    @NotNull
    private Collection<Suggestion> suggestionsFromDynamicExpression(CompletionFinder completionFinder) {
        String unwrap = ScriptUtils.unwrap(dynamicExpression);
        List<String> tokens = new TokenFinder().find(unwrap, unwrap.length());
        String[] strings = tokens.toArray(new String[0]);
        // TODO: Fixme.
        return completionFinder.find(TypeDefault.MESSAGE_AND_CONTEXT, strings, previousOutput);
    }
}
