package com.reedelk.plugin.service.module.impl.component.metadata;

import com.reedelk.plugin.service.module.impl.component.completion.*;
import com.reedelk.runtime.api.commons.StringUtils;

import java.util.*;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.*;

public class PreviousComponentOutputJoin extends AbstractPreviousComponentOutput {

    private final List<PreviousComponentOutput> outputs;

    public PreviousComponentOutputJoin(List<PreviousComponentOutput> outputs) {
        this.outputs = outputs;
    }

    @Override
    public Collection<Suggestion> buildDynamicSuggestions(SuggestionFinder suggestionFinder, Suggestion suggestion, TypeAndTries typeAndTrieMap, boolean flatten) {
        // TODO: Create artificial type proxy because it is a list of the suggestions ....

        // Output is List<Of all the types>
        List<Suggestion> suggestions = new ArrayList<>();
        outputs.forEach(previousComponentOutput ->
                suggestions.addAll(previousComponentOutput.buildDynamicSuggestions(suggestionFinder, suggestion, typeAndTrieMap, flatten)));
        return suggestions;
    }

    @Override
    public String description() {
        return StringUtils.EMPTY;
    }

    @Override
    public MetadataTypeDTO mapAttributes(SuggestionFinder suggestionFinder, TypeAndTries typeAndTries) {
        List<MetadataTypeDTO> attributesToMerge = outputs.stream()
                .map(previousComponentOutput -> previousComponentOutput.mapAttributes(suggestionFinder, typeAndTries))
                .collect(toList());
        return PreviousComponentOutputDefault.mergeMetadataTypes(attributesToMerge, typeAndTries);
    }

    @Override
    public List<MetadataTypeDTO> mapPayload(SuggestionFinder suggestionFinder, TypeAndTries typeAndTries) {
        // The output is a List of types, if the types are all the same
        List<MetadataTypeDTO> allTypes = new ArrayList<>();

        outputs.stream()
                .map(previousComponentOutput -> previousComponentOutput.mapPayload(suggestionFinder, typeAndTries))
                .forEach(allTypes::addAll);

        // Logic should be if all types have the same type
        Map<TypeProxy, List<MetadataTypeDTO>> collect = allTypes
                .stream()
                .collect(groupingBy(MetadataTypeDTO::getTypeProxy));

        if (collect.size() == 1) {
            // All the outputs have the same type (we must unroll the list type properties (List<Type> : Type)
            Map.Entry<TypeProxy, List<MetadataTypeDTO>> next = collect.entrySet().iterator().next();
            TypeProxy typeProxy = next.getKey();
            // Otherwise it would be a list of lists
            if (!typeProxy.isList(typeAndTries)) {
                OnTheFlyTypeProxy onTheFlyTypeProxy = new OnTheFlyTypeProxy(typeProxy.getTypeFullyQualifiedName());
                MetadataTypeDTO sameTypeElementsList = unrollListType(suggestionFinder, typeAndTries, onTheFlyTypeProxy);
                return singletonList(sameTypeElementsList);
            } else {
                // Output List<List<Type1>> ... do not unroll the properties
                MetadataTypeDTO metadataTypeDTO = new MetadataTypeDTO("List<" + typeProxy.toSimpleName(typeAndTries) + ">",
                        TypeProxy.create(List.class),
                        emptyList());
                return singletonList(metadataTypeDTO);
            }
        } else {
            // Output List<Type1,Type2,Type3> ... do not unroll the properties
            String listArgs = collect.keySet()
                    .stream()
                    .map(theType -> theType.toSimpleName(typeAndTries))
                    .collect(joining(","));
            MetadataTypeDTO metadataTypeDTO = new MetadataTypeDTO("List<" + listArgs + ">",
                    TypeProxy.create(List.class),
                    emptyList());
            return singletonList(metadataTypeDTO);
        }
    }


    static class OnTheFlyTypeProxy implements TypeProxy {

        private final String listItem;

        public OnTheFlyTypeProxy(String listItem) {
            this.listItem = listItem;
        }

        @Override
        public boolean isList(TypeAndTries typeAndTries) {
            return true;
        }

        @Override
        public Trie resolve(TypeAndTries typeAndTries) {
            return typeAndTries.getOrDefault(List.class.getName());
        }

        @Override
        public String toSimpleName(TypeAndTries typeAndTries) {
            return FullyQualifiedName.formatList(listItem, typeAndTries);
        }

        @Override
        public String getTypeFullyQualifiedName() {
            return List.class.getSimpleName();
        }

        @Override
        public TypeProxy listItemType(TypeAndTries typeAndTries) {
            return TypeProxy.create(listItem);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            OnTheFlyTypeProxy that = (OnTheFlyTypeProxy) o;
            return Objects.equals(listItem, that.listItem);
        }

        @Override
        public int hashCode() {
            return Objects.hash(listItem);
        }
    }
}
