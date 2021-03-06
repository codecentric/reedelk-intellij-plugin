package de.codecentric.reedelk.plugin.service.module.impl.component.metadata;

import de.codecentric.reedelk.plugin.service.module.impl.component.completion.Suggestion;
import de.codecentric.reedelk.plugin.service.module.impl.component.completion.SuggestionFinder;
import de.codecentric.reedelk.plugin.service.module.impl.component.completion.TypeAndTries;
import de.codecentric.reedelk.plugin.service.module.impl.component.completion.TypeProxy;
import de.codecentric.reedelk.runtime.api.message.MessageAttributes;
import de.codecentric.reedelk.runtime.api.message.MessagePayload;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static de.codecentric.reedelk.runtime.api.commons.Preconditions.checkNotNull;

public class PreviousComponentOutputCompound implements PreviousComponentOutput {

    private final PreviousComponentOutput attributes;
    private final PreviousComponentOutput payload;

    public PreviousComponentOutputCompound(PreviousComponentOutput attributes, PreviousComponentOutput payload) {
        checkNotNull(attributes, "attributes");
        checkNotNull(payload, "payload");
        this.attributes = attributes;
        this.payload = payload;
    }

    @Override
    public Collection<Suggestion> buildDynamicSuggestions(@NotNull SuggestionFinder suggester,
                                                          @NotNull Suggestion suggestion,
                                                          @NotNull TypeAndTries typeAndTrieMap) {
        TypeProxy suggestionType = suggestion.getReturnType();
        if (MessageAttributes.class.getName().equals(suggestionType.getTypeFullyQualifiedName())) {
            return attributes.buildDynamicSuggestions(suggester, suggestion, typeAndTrieMap);
        } else if (MessagePayload.class.getName().equals(suggestionType.getTypeFullyQualifiedName())) {
            return payload.buildDynamicSuggestions(suggester, suggestion, typeAndTrieMap);
        }  else {
            throw new IllegalStateException("Resolve must be called only if the suggestion type is dynamic");
        }
    }

    @Override
    public String description() {
        return payload.description();
    }

    @Override
    public MetadataTypeDTO mapAttributes(@NotNull SuggestionFinder suggester, @NotNull TypeAndTries typeAndTries) {
        return attributes.mapAttributes(suggester, typeAndTries);
    }

    @Override
    public List<MetadataTypeDTO> mapPayload(@NotNull SuggestionFinder suggester, @NotNull TypeAndTries typeAndTries) {
        return payload.mapPayload(suggester, typeAndTries);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PreviousComponentOutputCompound that = (PreviousComponentOutputCompound) o;
        return Objects.equals(attributes, that.attributes) &&
                Objects.equals(payload, that.payload);
    }

    @Override
    public int hashCode() {
        return Objects.hash(attributes, payload);
    }

    @Override
    public String toString() {
        return "PreviousComponentOutputCompound{" +
                "attributes=" + attributes +
                ", payload=" + payload +
                '}';
    }
}
