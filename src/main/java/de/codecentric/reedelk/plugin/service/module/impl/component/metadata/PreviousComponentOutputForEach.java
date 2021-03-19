package de.codecentric.reedelk.plugin.service.module.impl.component.metadata;

import de.codecentric.reedelk.plugin.service.module.impl.component.completion.Suggestion;
import de.codecentric.reedelk.plugin.service.module.impl.component.completion.TypeProxy;
import de.codecentric.reedelk.plugin.service.module.impl.component.completion.SuggestionFinder;
import de.codecentric.reedelk.plugin.service.module.impl.component.completion.TypeAndTries;
import de.codecentric.reedelk.runtime.api.commons.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

// Unwraps the list item type if the previous output is a list of any type.
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

        List<Suggestion> outSuggestions = new ArrayList<>();

        for (Suggestion next : suggestions) {
            // We extract the type of the list items.
            if (next.getReturnType().resolve(typeAndTrieMap).isList()) {
                // Create a suggestion with only the list item type as return type.
                TypeProxy listItemTypeProxy = next.getReturnType().resolve(typeAndTrieMap).listItemType(typeAndTrieMap);
                Suggestion unrolledListItem = Suggestion.create(suggestion.getType())
                        .cursorOffset(suggestion.getCursorOffset())
                        .insertValue(suggestion.getInsertValue())
                        .lookupToken(suggestion.getLookupToken())
                        .tailText(suggestion.getTailText())
                        .returnTypeDisplayValue(listItemTypeProxy.toSimpleName(typeAndTrieMap))
                        .returnType(listItemTypeProxy)
                        .build();
                outSuggestions.add(unrolledListItem);
            } else {
                outSuggestions.add(next);
            }
        }

        return outSuggestions;
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
        List<MetadataTypeDTO> typeDTOList = previousComponentOutput.mapPayload(suggestionFinder, typeAndTries);
        List<MetadataTypeDTO> outDTOList = new ArrayList<>();

        // Unroll list types.
        for (MetadataTypeDTO metadataTypeDTO : typeDTOList) {
            TypeProxy typeProxy = metadataTypeDTO.getTypeProxy();
            // we extract the item type from the list, provided the type is a list.
            if (typeProxy.resolve(typeAndTries).isList()) {
                TypeProxy listItemTypeProxy = typeProxy.resolve(typeAndTries).listItemType(typeAndTries);
                MetadataTypeDTO unrolledList = new MetadataTypeDTO(
                        listItemTypeProxy.toSimpleName(typeAndTries),
                        listItemTypeProxy,
                        metadataTypeDTO.getProperties());
                outDTOList.add(unrolledList);
            } else {
                outDTOList.add(metadataTypeDTO);
            }
        }

        return outDTOList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PreviousComponentOutputForEach that = (PreviousComponentOutputForEach) o;
        return Objects.equals(previousComponentOutput, that.previousComponentOutput);
    }

    @Override
    public int hashCode() {
        return Objects.hash(previousComponentOutput);
    }

    @Override
    public String toString() {
        return "PreviousComponentOutputForEach{" +
                "previousComponentOutput=" + previousComponentOutput +
                '}';
    }
}
