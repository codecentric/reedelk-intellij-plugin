package com.reedelk.plugin.service.module.impl.component.metadata;

import com.reedelk.plugin.service.module.impl.component.completion.*;
import com.reedelk.runtime.api.message.MessageAttributes;
import com.reedelk.runtime.api.message.MessagePayload;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

public class PreviousComponentOutputDefault extends AbstractPreviousComponentOutput {

    private List<String> attributes;
    private List<String> payload;
    private String description;

    public PreviousComponentOutputDefault(List<String> attributes, List<String> payload, String description) {
        this.description = description;
        this.attributes = attributes == null || attributes.isEmpty() ?
                singletonList(TypeDefault.DEFAULT_ATTRIBUTES) : attributes;
        this.payload = payload == null || payload.isEmpty() ?
                singletonList(TypeDefault.DEFAULT_PAYLOAD) : payload;
    }

    @Override
    public Collection<Suggestion> buildDynamicSuggestions(@NotNull SuggestionFinder suggestionFinder,
                                                          @NotNull Suggestion suggestion,
                                                          @NotNull TypeAndTries typeAndTrieMap) {
        return resolveDynamicTypes(suggestion)
                .stream()
                .map(TypeProxy::create)
                .map(dynamicType -> Suggestion.create(suggestion.getType())
                        .cursorOffset(suggestion.getCursorOffset())
                        .insertValue(suggestion.getInsertValue())
                        .lookupToken(suggestion.getLookupToken())
                        .tailText(suggestion.getTailText())
                        .returnTypeDisplayValue(dynamicType.toSimpleName(typeAndTrieMap))
                        .returnType(dynamicType)
                        .build())
                .collect(toList());
    }

    @Override
    public String description() {
        return description;
    }

    @Override
    public MetadataTypeDTO mapAttributes(@NotNull SuggestionFinder suggester, @NotNull TypeAndTries typeAndTries) {
        return attributes(suggester, typeAndTries);
    }

    @Override
    public List<MetadataTypeDTO> mapPayload(@NotNull SuggestionFinder suggester, @NotNull TypeAndTries typeAndTries) {
        return payload(suggester, typeAndTries);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PreviousComponentOutputDefault that = (PreviousComponentOutputDefault) o;
        return Objects.equals(attributes, that.attributes) &&
                Objects.equals(payload, that.payload) &&
                Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(attributes, payload, description);
    }

    // Resolves the dynamic type from the output descriptor
    protected List<String> resolveDynamicTypes(Suggestion suggestion) {
        TypeProxy suggestionType = suggestion.getReturnType();
        if (MessageAttributes.class.getName().equals(suggestionType.getTypeFullyQualifiedName())) {
            return attributes.isEmpty() ? singletonList(TypeDefault.DEFAULT_ATTRIBUTES) : attributes;
        } else if (MessagePayload.class.getName().equals(suggestionType.getTypeFullyQualifiedName())) {
            return payload.isEmpty() ? singletonList(TypeDefault.DEFAULT_PAYLOAD) : payload;
        } else {
            throw new IllegalStateException("Resolve must be called only if the suggestion type is dynamic");
        }
    }

    protected MetadataTypeDTO attributes(SuggestionFinder suggester, TypeAndTries typeAndTries) {
        List<MetadataTypeDTO> metadataTypes = attributes.stream()
                .distinct() // we want to avoid creating data for the same type.
                .map(attributeType -> createMetadataType(suggester, typeAndTries, TypeProxy.create(attributeType)))
                .collect(toList());
        return mergeMetadataTypes(metadataTypes, typeAndTries);
    }

    protected List<MetadataTypeDTO> payload(SuggestionFinder suggester, TypeAndTries typeAndTries) {
        return payload.stream()
                .distinct()
                .map(payloadType -> createMetadataType(suggester, typeAndTries, TypeProxy.create(payloadType)))
                .collect(toList());
    }
}
