package de.codecentric.reedelk.plugin.service.module.impl.component.metadata;

import de.codecentric.reedelk.plugin.service.module.impl.component.completion.*;
import de.codecentric.reedelk.runtime.api.message.MessageAttributes;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static de.codecentric.reedelk.runtime.api.commons.StringUtils.EMPTY;
import static de.codecentric.reedelk.runtime.api.commons.StringUtils.isNotBlank;
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
            // the properties, it does not creates a list for each type, therefore we can just return the
            // suggestions attributes for each previous component output.
            return suggestions;
        }


        // Otherwise we group for each output return type and create a List<Type1,Type2,...>
        Map<TypeProxy, List<Suggestion>> suggestionByReturnType = suggestions
                .stream()
                .collect(groupingBy(Suggestion::getReturnType));

        // All the suggestions have the same return type.
        if (suggestionByReturnType.size() == 1) {
            Map.Entry<TypeProxy, List<Suggestion>> next = suggestionByReturnType.entrySet().iterator().next();
            TypeProxy theType = next.getKey();
            OnTheFlyTypeProxy proxy = new OnTheFlyTypeProxy(theType.getTypeFullyQualifiedName());
            Suggestion suggestionAsList = SuggestionFactory.copyWithType(typeAndTrieMap, suggestion, proxy);
            return Collections.singletonList(suggestionAsList);

        } else if (suggestionByReturnType.size() == 0) {
            OnTheFlyTypeProxy proxy = new OnTheFlyTypeProxy(Object.class.getName(), Object.class.getSimpleName());
            Suggestion suggestionAsList = SuggestionFactory.copyWithType(typeAndTrieMap, suggestion, proxy);
            return Collections.singletonList(suggestionAsList);

        } else {
            // Otherwise we must join the return types all together and display List<Type1,Type2 ...>.
            // In this case the list item type is generic object since it is a mix of different object types.
            String listArgs = suggestionByReturnType.keySet()
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
        return EMPTY;
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
        List<MetadataTypeDTO> metadataTypeList = new ArrayList<>();
        outputs.stream()
                .map(previousComponentOutput -> previousComponentOutput.mapPayload(suggester, typeAndTries))
                .forEach(metadataTypeList::addAll);

        // We group all the metadata DTOs by their type:
        // If they all have the same type, then we display a single list of type: List<MyType> and we unroll the list item type.
        // Otherwise the output will be List<Type1,Type2,Type3> (single type properties are not unrolled)
        Map<TypeProxy, List<MetadataTypeDTO>> metadataGroupedByType = metadataTypeList
                .stream()
                .collect(groupingBy(MetadataTypeDTO::getTypeProxy));

        // All the outputs have the same type. If it is a list, we unroll the list item type properties
        // to give the user better info about the type of the list: List<Type> : Type.
        if (metadataGroupedByType.size() == 1) {
            Map.Entry<TypeProxy, List<MetadataTypeDTO>> metadataTypeAndDTOList = metadataGroupedByType.entrySet().iterator().next();
            TypeProxy typeProxy = metadataTypeAndDTOList.getKey();

            // If the type is a list, the final type is List<List<Type>>,
            // therefore we do not unroll the list item properties.
            if (typeProxy.resolve(typeAndTries).isList()) {
                String listSimpleName = typeProxy.toSimpleName(typeAndTries); // List<Type>
                String listOfList = TrieList.formatList(listSimpleName);    // List<List<Type>>
                MetadataTypeDTO metadataTypeDTO = new MetadataTypeDTO(listOfList, TypeProxy.create(List.class), emptyList());
                return singletonList(metadataTypeDTO);

            } else {
                // If the type is not a list, we unroll the properties of the type.
                OnTheFlyTypeProxy proxy = new OnTheFlyTypeProxy(typeProxy.getTypeFullyQualifiedName());
                MetadataTypeDTO sameTypeElementsList = unrollListType(suggester, typeAndTries, proxy);
                return singletonList(sameTypeElementsList);
            }

        } else if (metadataGroupedByType.size() == 0) {
            // If there are no outputs, we just display generic List<Object> type.
            String listOfObject = TrieList.formatList(Object.class.getSimpleName());
            MetadataTypeDTO metadataTypeDTO = new MetadataTypeDTO(listOfObject, TypeProxy.create(List.class), emptyList());
            return singletonList(metadataTypeDTO);

        } else {
            // Output List<Type1,Type2,Type3> ... do not unroll the properties
            String listArgs = metadataGroupedByType.keySet()
                    .stream()
                    .map(theType -> theType.toSimpleName(typeAndTries))
                    .collect(joining(","));
            String listDisplayType = TrieList.formatList(listArgs);
            MetadataTypeDTO metadataTypeDTO = new MetadataTypeDTO(listDisplayType, TypeProxy.create(List.class), emptyList());
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

        public OnTheFlyTypeProxy(String listItemType) {
            super(List.class.getName());
            this.displayName = null;
            this.trieList = new TrieList(List.class.getName(), List.class.getName(), null, listItemType);
        }

        public OnTheFlyTypeProxy(String listItemType, String displayName) {
            super(List.class.getName());
            this.displayName = TrieList.formatList(displayName);
            this.trieList = new TrieList(List.class.getName(), List.class.getName(), this.displayName, listItemType);
        }

        @Override
        public Trie resolve(TypeAndTries typeAndTries) {
            return trieList;
        }

        @Override
        public String toSimpleName(TypeAndTries typeAndTries) {
            return isNotBlank(displayName) ?
                    displayName :
                    trieList.toSimpleName(typeAndTries);
        }
    }
}
