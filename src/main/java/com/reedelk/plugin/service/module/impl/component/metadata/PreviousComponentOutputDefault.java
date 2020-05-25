package com.reedelk.plugin.service.module.impl.component.metadata;

import com.reedelk.plugin.service.module.impl.component.completion.Suggestion;
import com.reedelk.plugin.service.module.impl.component.completion.SuggestionFinder;
import com.reedelk.plugin.service.module.impl.component.completion.TypeAndTries;
import com.reedelk.plugin.service.module.impl.component.completion.TypeProxy;
import com.reedelk.runtime.api.message.MessageAttributes;
import com.reedelk.runtime.api.message.MessagePayload;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static com.reedelk.plugin.service.module.impl.component.completion.TypeDefault.DEFAULT_ATTRIBUTES;
import static com.reedelk.plugin.service.module.impl.component.completion.TypeDefault.DEFAULT_PAYLOAD;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

public class PreviousComponentOutputDefault extends AbstractPreviousComponentOutput {

    private List<String> attributes;
    private List<String> payload;
    private String description;

    public PreviousComponentOutputDefault(List<String> attributes, List<String> payload) {
        this(attributes, payload, null);
    }

    public PreviousComponentOutputDefault(List<String> attributes, List<String> payload, String description) {
        this.description = description;
        this.attributes = attributes == null || attributes.isEmpty() ? singletonList(DEFAULT_ATTRIBUTES) : attributes;
        this.payload = payload == null || payload.isEmpty() ? singletonList(DEFAULT_PAYLOAD) : payload;
    }

    @Override
    public Collection<Suggestion> buildDynamicSuggestions(@NotNull SuggestionFinder suggestionFinder,
                                                          @NotNull Suggestion suggestion,
                                                          @NotNull TypeAndTries typeAndTrieMap) {
        return resolveDynamicTypes(suggestion)
                .stream()
                .map(TypeProxy::create)
                .map(dynamicType -> Suggestion.create(suggestion.getType())
                        .returnTypeDisplayValue(dynamicType.toSimpleName(typeAndTrieMap))
                        .cursorOffset(suggestion.getCursorOffset())
                        .insertValue(suggestion.getInsertValue())
                        .lookupToken(suggestion.getLookupToken())
                        .tailText(suggestion.getTailText())
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
        List<MetadataTypeDTO> attributesMetadataTypes = createMetadataTypesFrom(attributes, suggester, typeAndTries);
        return MetadataUtils.mergeAttributesMetadata(attributesMetadataTypes, typeAndTries);
    }

    @Override
    public List<MetadataTypeDTO> mapPayload(@NotNull SuggestionFinder suggester, @NotNull TypeAndTries typeAndTries) {
        return createMetadataTypesFrom(payload, suggester, typeAndTries);
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

    @Override
    public String toString() {
        return "PreviousComponentOutputDefault{" +
                "attributes=" + attributes +
                ", payload=" + payload +
                ", description='" + description + '\'' +
                '}';
    }

    // Resolves the dynamic type from the output descriptor
    private List<String> resolveDynamicTypes(Suggestion suggestion) {
        TypeProxy suggestionType = suggestion.getReturnType();
        if (MessageAttributes.class.getName().equals(suggestionType.getTypeFullyQualifiedName())) {
            return attributes.isEmpty() ? singletonList(DEFAULT_ATTRIBUTES) : attributes;
        } else if (MessagePayload.class.getName().equals(suggestionType.getTypeFullyQualifiedName())) {
            return payload.isEmpty() ? singletonList(DEFAULT_PAYLOAD) : payload;
        } else {
            throw new IllegalStateException("Resolve must be called only if the suggestion type is dynamic");
        }
    }

    private List<MetadataTypeDTO> createMetadataTypesFrom(List<String> types, SuggestionFinder suggester, TypeAndTries typeAndTries) {
        return types.stream()
                .distinct()
                .map(theType -> createMetadataType(suggester, typeAndTries, TypeProxy.create(theType)))
                .collect(toList());
    }
}
