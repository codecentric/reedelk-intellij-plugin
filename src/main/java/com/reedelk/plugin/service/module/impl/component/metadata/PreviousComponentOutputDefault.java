package com.reedelk.plugin.service.module.impl.component.metadata;

import com.reedelk.plugin.exception.PluginException;
import com.reedelk.plugin.service.module.impl.component.completion.*;
import com.reedelk.runtime.api.message.MessageAttributes;
import com.reedelk.runtime.api.message.MessagePayload;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

public class PreviousComponentOutputDefault extends AbstractPreviousComponentOutput {

    private List<String> attributes;
    private List<String> payload;
    private String description;

    public PreviousComponentOutputDefault(List<String> attributes, List<String> payload, String description) {
        this.description = description;
        this.attributes = attributes == null || attributes.isEmpty() ?
                Collections.singletonList(MessageAttributes.class.getName()) : attributes;
        this.payload = payload == null || payload.isEmpty() ?
                Collections.singletonList(Object.class.getName()) : payload;
    }

    @Override
    public Collection<Suggestion> buildDynamicSuggestions(Suggestion suggestion, TypeAndTries typeAndTrieMap, boolean flatten) {
        List<Suggestion> dynamicSuggestions = resolve(suggestion)
                .stream()
                .map(dynamicType -> Suggestion.create(suggestion.getType())
                        .cursorOffset(suggestion.getCursorOffset())
                        .insertValue(suggestion.getInsertValue())
                        .lookupToken(suggestion.getLookupToken())
                        .tailText(suggestion.getTailText())
                        .returnTypeDisplayValue(TypeUtils.toSimpleName(dynamicType, typeAndTrieMap, suggestion))
                        .returnType(dynamicType)
                        .build())
                .collect(toList());
        return flatten ?
                Collections.singletonList(flatten(dynamicSuggestions, typeAndTrieMap)) :
                dynamicSuggestions;
    }

    @Override
    public String description() {
        return description;
    }

    @Override
    public MetadataTypeDTO mapAttributes(CompletionFinder completionFinder, TypeAndTries typeAndTries) {
        return attributes(completionFinder, typeAndTries);
    }

    @Override
    public List<MetadataTypeDTO> mapPayload(CompletionFinder completionFinder, TypeAndTries typeAndTries) {
        return payload(completionFinder, typeAndTries);
    }

    // Resolves the dynamic type from the output descriptor
    private List<String> resolve(Suggestion suggestion) {
        String suggestionType = suggestion.getReturnType();
        if (MessageAttributes.class.getName().equals(suggestionType)) {
            return attributes.isEmpty() ? singletonList(MessageAttributes.class.getName()) : attributes;
        } else if (MessagePayload.class.getName().equals(suggestionType)) {
            return payload.isEmpty() ? singletonList(Object.class.getName()) : payload;
        } else {
            throw new IllegalStateException("Resolve must be called only if the suggestion type is dynamic");
        }
    }

    /**
     * Takes a group of suggestions which refer to the same lookup string and collapses them into one
     * single suggestion with type equal to a comma separated list of all the suggestions in the group.
     */
    private Suggestion flatten(Collection<Suggestion> suggestions, TypeAndTries typeAndTrieMap) {
        Suggestion suggestion = suggestions.stream()
                .findAny()
                .orElseThrow(() -> new PluginException("Expected at least one dynamic suggestion."));
        List<String> possibleTypes = suggestions.stream()
                .map(theSuggestion -> TypeUtils.toSimpleName(theSuggestion.getReturnType(), typeAndTrieMap))
                .collect(toList());
        return Suggestion.create(suggestion.getType())
                .insertValue(suggestion.getInsertValue())
                .tailText(suggestion.getTailText())
                .lookupToken(suggestion.getLookupToken())
                // The return type for 'flattened' suggestions is never used because this suggestion is only created for a terminal token.
                .returnType(Default.DEFAULT_RETURN_TYPE)
                .returnTypeDisplayValue(String.join(",", possibleTypes))
                .build();
    }

    protected MetadataTypeDTO attributes(CompletionFinder completionFinder, TypeAndTries typeAndTries) {
        List<MetadataTypeDTO> metadataTypes = attributes.stream()
                        .distinct() // we want to avoid creating data for the same type.
                        .map(attributeType -> createMetadataType(completionFinder, typeAndTries, attributeType))
                        .collect(toList());
        return mergeMetadataTypes(metadataTypes);
    }

    protected List<MetadataTypeDTO> payload(CompletionFinder completionFinder, TypeAndTries typeAndTries) {
        return payload.stream()
                .distinct()
                .map(payloadType -> createMetadataType(completionFinder, typeAndTries, payloadType))
                .collect(toList());
    }
}
