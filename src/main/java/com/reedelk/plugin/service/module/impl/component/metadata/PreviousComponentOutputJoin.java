package com.reedelk.plugin.service.module.impl.component.metadata;

import com.reedelk.plugin.service.module.impl.component.completion.*;
import com.reedelk.runtime.api.commons.StringUtils;
import com.reedelk.runtime.api.message.MessageAttributes;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.*;

public class PreviousComponentOutputJoin extends AbstractPreviousComponentOutput {

    private final Set<PreviousComponentOutput> outputs;

    public PreviousComponentOutputJoin(Set<PreviousComponentOutput> outputs) {
        this.outputs = outputs;
    }

    @Override
    public Collection<Suggestion> buildDynamicSuggestions(@NotNull SuggestionFinder suggester,
                                                          @NotNull Suggestion suggestion,
                                                          @NotNull TypeAndTries typeAndTrieMap) {

        List<Suggestion> suggestions = new ArrayList<>();
        // We need to compute the output for all the branches joining the node.
        // They all going to be in a list of objects.
        for (PreviousComponentOutput output : outputs) {
            Collection<Suggestion> suggestionsForOutput =
                    output.buildDynamicSuggestions(suggester, suggestion, typeAndTrieMap);
            suggestions.addAll(suggestionsForOutput);
        }

        if (MessageAttributes.class.getName().equals(suggestion.getReturnType().getTypeFullyQualifiedName())) {
            // If the dynamic suggestions we are asking for are the message attributes, then the JOIN joins
            // the properties, it does not creates a list, therefore we can just return the suggestions attributes
            // for each previous component output.
            return suggestions;
        }


        // Otherwise we group for each output type and create a List<Type1,Type2,...>
        Map<TypeProxy, List<Suggestion>> suggestionsByType = suggestions
                .stream()
                .collect(groupingBy(Suggestion::getReturnType));

        if (suggestionsByType.size() == 1) {
            Map.Entry<TypeProxy, List<Suggestion>> next = suggestionsByType.entrySet().iterator().next();
            TypeProxy listItemType = next.getKey();
            OnTheFlyTypeProxy proxy = new OnTheFlyTypeProxy(listItemType.getTypeFullyQualifiedName());
            Suggestion suggestionAsList = SuggestionFactory.copyWithType(typeAndTrieMap, suggestion, proxy);
            return Collections.singletonList(suggestionAsList);

        } else {
            String listArgs = suggestionsByType.keySet()
                    .stream()
                    .map(theType -> theType.toSimpleName(typeAndTrieMap))
                    .collect(joining(","));
            OnTheFlyTypeProxy proxy = new OnTheFlyTypeProxy(Object.class.getName(), listArgs);
            Suggestion suggestionAsList = SuggestionFactory.copyWithType(typeAndTrieMap, suggestion, proxy);
            return Collections.singletonList(suggestionAsList);
        }
    }

    @Override
    public String description() {
        return StringUtils.EMPTY;
    }

    @Override
    public MetadataTypeDTO mapAttributes(@NotNull SuggestionFinder suggester, @NotNull TypeAndTries typeAndTries) {
        List<MetadataTypeDTO> attributesToMerge = outputs.stream()
                .map(previousComponentOutput -> previousComponentOutput.mapAttributes(suggester, typeAndTries))
                .collect(toList());
        return MetadataUtils.mergeAttributesMetadata(attributesToMerge, typeAndTries);
    }

    @Override
    public List<MetadataTypeDTO> mapPayload(@NotNull SuggestionFinder suggester, @NotNull TypeAndTries typeAndTries) {
        // The output is a List of types, if the types are all the same
        List<MetadataTypeDTO> allTypes = new ArrayList<>();

        outputs.stream()
                .map(previousComponentOutput -> previousComponentOutput.mapPayload(suggester, typeAndTries))
                .forEach(allTypes::addAll);

        // We group all the metadata DTOs by their type:
        // If they all have the same type, then we display a single list of type: List<MyType> and we unroll the list item type.
        // Otherwise the output will be List<Type1,Type2,Type3> (single type properties are not unrolled)
        Map<TypeProxy, List<MetadataTypeDTO>> metadataByType = allTypes
                .stream()
                .collect(groupingBy(MetadataTypeDTO::getTypeProxy));

        if (metadataByType.size() == 1) {
            // All the outputs have the same type (we must unroll the list type properties (List<Type> : Type)
            Map.Entry<TypeProxy, List<MetadataTypeDTO>> next = metadataByType.entrySet().iterator().next();
            TypeProxy typeProxy = next.getKey();
            // Otherwise it would be a list of lists
            if (typeProxy.resolve(typeAndTries).isList()) {
                // Output List<List<Type1>> ... do not unroll the properties
                String listSimpleName = typeProxy.toSimpleName(typeAndTries);
                MetadataTypeDTO metadataTypeDTO =
                        new MetadataTypeDTO(listSimpleName, TypeProxy.create(List.class), emptyList());
                return singletonList(metadataTypeDTO);

            } else {
                OnTheFlyTypeProxy proxy = new OnTheFlyTypeProxy(typeProxy.getTypeFullyQualifiedName());
                MetadataTypeDTO sameTypeElementsList = unrollListType(suggester, typeAndTries, proxy);
                return singletonList(sameTypeElementsList);
            }
        } else {
            // Output List<Type1,Type2,Type3> ... do not unroll the properties
            String listArgs = metadataByType.keySet()
                    .stream()
                    .map(theType -> theType.toSimpleName(typeAndTries))
                    .collect(joining(","));
            MetadataTypeDTO metadataTypeDTO = new MetadataTypeDTO("List<" + listArgs + ">",
                    TypeProxy.create(List.class),
                    emptyList());
            return singletonList(metadataTypeDTO);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PreviousComponentOutputJoin that = (PreviousComponentOutputJoin) o;
        return Objects.equals(outputs, that.outputs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(outputs);
    }

    @Override
    public String toString() {
        return "PreviousComponentOutputJoin{" +
                "outputs=" + outputs +
                '}';
    }

    static class OnTheFlyTypeProxy extends TypeProxyDefault {

        private final TrieList trieList;
        private final String displayName;

        public OnTheFlyTypeProxy(String listItem) {
            super(List.class.getSimpleName());
            this.displayName = null;
            this.trieList = new TrieList(List.class.getName(), List.class.getName(), null, listItem);
        }

        public OnTheFlyTypeProxy(String itemType, String displayName) {
            super(List.class.getSimpleName());
            this.displayName = String.format(TrieList.FORMAT_LIST, displayName);
            this.trieList = new TrieList(List.class.getName(), List.class.getName(), this.displayName, itemType);
        }

        @Override
        public Trie resolve(TypeAndTries typeAndTries) {
            return trieList;
        }

        @Override
        public String toSimpleName(TypeAndTries typeAndTries) {
            if (StringUtils.isNotBlank(displayName)) {
                return displayName;
            } else {
                return trieList.toSimpleName(typeAndTries);
            }
        }
    }
}
