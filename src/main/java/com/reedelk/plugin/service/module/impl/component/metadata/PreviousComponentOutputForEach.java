package com.reedelk.plugin.service.module.impl.component.metadata;

import com.reedelk.plugin.service.module.impl.component.completion.Suggestion;
import com.reedelk.plugin.service.module.impl.component.completion.SuggestionFinder;
import com.reedelk.plugin.service.module.impl.component.completion.TypeAndTries;
import com.reedelk.plugin.service.module.impl.component.completion.TypeProxy;
import com.reedelk.runtime.api.commons.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class PreviousComponentOutputForEach extends AbstractPreviousComponentOutput {

    private final PreviousComponentOutput previousComponentOutput;

    public PreviousComponentOutputForEach(PreviousComponentOutput previousComponentOutput) {
        this.previousComponentOutput = previousComponentOutput;
    }

    @Override
    public Collection<Suggestion> buildDynamicSuggestions(@NotNull SuggestionFinder suggester,
                                                          @NotNull Suggestion suggestion,
                                                          @NotNull TypeAndTries typeAndTrieMap) {
        Collection<Suggestion> suggestions =
                previousComponentOutput.buildDynamicSuggestions(suggester, suggestion, typeAndTrieMap);
        // We need to provide alternatives, and flatten only at the end
        if (suggestions.size() == 1) {
            // You need to extract  the list of all predecessors
            // TODO: You might have output suggestions with multiple list of different types, you
            //  should merge them all... this might happen when the for each joins a fork with List<Type1>, List<Type2>
            // TODO: What if it is a list of lists!?
            Suggestion next = suggestions.iterator().next();
            if (next.getReturnType().resolve(typeAndTrieMap).isList()) {
                // Create a suggestion with only return type.
                TypeProxy listItemTypeProxy = next.getReturnType().resolve(typeAndTrieMap).listItemType(typeAndTrieMap);
                return Collections.singletonList(Suggestion.create(suggestion.getType())
                        .cursorOffset(suggestion.getCursorOffset())
                        .insertValue(suggestion.getInsertValue())
                        .lookupToken(suggestion.getLookupToken())
                        .tailText(suggestion.getTailText())
                        .returnTypeDisplayValue(listItemTypeProxy.toSimpleName(typeAndTrieMap))
                        .returnType(listItemTypeProxy)
                        .build());
            }
        }
        return suggestions;
    }

    @Override
    public String description() {
        return StringUtils.EMPTY;
    }

    @Override
    public MetadataTypeDTO mapAttributes(@NotNull SuggestionFinder suggestionFinder, @NotNull TypeAndTries typeAndTries) {
        return previousComponentOutput.mapAttributes(suggestionFinder, typeAndTries);
    }

    @Override
    public List<MetadataTypeDTO> mapPayload(@NotNull SuggestionFinder suggestionFinder, @NotNull TypeAndTries typeAndTries) {
        List<MetadataTypeDTO> metadataTypeDTOS = previousComponentOutput.mapPayload(suggestionFinder, typeAndTries);
        if (metadataTypeDTOS.size() == 1) {
            MetadataTypeDTO metadataTypeDTO = metadataTypeDTOS.stream().findFirst().get();
            TypeProxy typeProxy = metadataTypeDTO.getTypeProxy();

            // we extract the items one by one from the for each list.
            // TODO: This is maybe applicable for maps as well!?
            if (typeProxy.resolve(typeAndTries).isList()) {
                TypeProxy listItemTypeProxy = typeProxy.resolve(typeAndTries).listItemType(typeAndTries);
                MetadataTypeDTO unrolledList = new MetadataTypeDTO(
                        listItemTypeProxy.toSimpleName(typeAndTries),
                        listItemTypeProxy,
                        metadataTypeDTO.getProperties());
                return Collections.singletonList(unrolledList);
            }
        }

        return metadataTypeDTOS;
    }
}
