package com.reedelk.plugin.service.module.impl.component.metadata;

import com.reedelk.plugin.service.module.impl.component.completion.Suggestion;
import com.reedelk.plugin.service.module.impl.component.completion.SuggestionFinder;
import com.reedelk.plugin.service.module.impl.component.completion.TypeAndTries;
import com.reedelk.runtime.api.message.MessageAttributes;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static java.util.stream.Collectors.toList;

public class PreviousComponentOutputMultipleMessages implements PreviousComponentOutput {

    private final Set<PreviousComponentOutput> outputs;

    public PreviousComponentOutputMultipleMessages(Set<PreviousComponentOutput> outputs) {
        this.outputs = outputs;
    }

    @Override
    public Collection<Suggestion> buildDynamicSuggestions(@NotNull SuggestionFinder suggester,
                                                          @NotNull Suggestion suggestion,
                                                          @NotNull TypeAndTries typeAndTrieMap) {
        if (MessageAttributes.class.getName().equals(suggestion.getReturnType().getTypeFullyQualifiedName())) {
            // If the dynamic suggestions we are asking for are the message attributes, then the JOIN joins
            // the properties, it does not creates a list for each type, therefore we can just return the
            // suggestions attributes for each previous component output.
            List<Suggestion> suggestions = new ArrayList<>();
            // We need to compute the output for all the branches joining the node.
            // They all going to be in a list of objects.
            for (PreviousComponentOutput output : outputs) {
                Collection<Suggestion> suggestionsForOutput =
                        output.buildDynamicSuggestions(suggester, suggestion, typeAndTrieMap);
                suggestions.addAll(suggestionsForOutput);
            }
            return suggestions;
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public String description() {
        throw new UnsupportedOperationException();
    }

    @Override
    public MetadataTypeDTO mapAttributes(@NotNull SuggestionFinder suggester, @NotNull TypeAndTries typeAndTries) {
        // The join merges the attributes all together in a single collection.
        List<MetadataTypeDTO> attributesToMerge = outputs.stream()
                .map(previousComponentOutput -> previousComponentOutput.mapAttributes(suggester, typeAndTries))
                .collect(toList());
        return MetadataUtils.mergeAttributesMetadata(attributesToMerge, typeAndTries);
    }

    @Override
    public List<MetadataTypeDTO> mapPayload(@NotNull SuggestionFinder suggester, @NotNull TypeAndTries typeAndTries) {
        throw new UnsupportedOperationException();
    }
}
