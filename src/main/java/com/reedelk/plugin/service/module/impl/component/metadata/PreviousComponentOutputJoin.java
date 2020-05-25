package com.reedelk.plugin.service.module.impl.component.metadata;

import com.reedelk.plugin.service.module.impl.component.completion.*;
import com.reedelk.runtime.api.commons.StringUtils;
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
        // TODO: Create artificial type proxy because it is a list of the suggestions ....

        // Output is List<Of all the types>
        List<Suggestion> suggestions = new ArrayList<>();
        outputs.forEach(previousComponentOutput ->
                suggestions.addAll(previousComponentOutput.buildDynamicSuggestions(suggester, suggestion, typeAndTrieMap)));
        return suggestions;
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

        // Logic should be if all types have the same type
        Map<TypeProxy, List<MetadataTypeDTO>> collect = allTypes
                .stream()
                .collect(groupingBy(MetadataTypeDTO::getTypeProxy));

        if (collect.size() == 1) {
            // All the outputs have the same type (we must unroll the list type properties (List<Type> : Type)
            Map.Entry<TypeProxy, List<MetadataTypeDTO>> next = collect.entrySet().iterator().next();
            TypeProxy typeProxy = next.getKey();
            // Otherwise it would be a list of lists
            if (typeProxy.resolve(typeAndTries).isList()) {
                // Output List<List<Type1>> ... do not unroll the properties
                String listSimpleName = typeProxy.toSimpleName(typeAndTries);
                MetadataTypeDTO metadataTypeDTO =
                        new MetadataTypeDTO(listSimpleName, TypeProxy.create(List.class), emptyList());
                return singletonList(metadataTypeDTO);

            } else {
                OnTheFlyTypeProxy onTheFlyTypeProxy = new OnTheFlyTypeProxy(typeProxy.getTypeFullyQualifiedName());
                MetadataTypeDTO sameTypeElementsList = unrollListType(suggester, typeAndTries, onTheFlyTypeProxy);
                return singletonList(sameTypeElementsList);
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

        public OnTheFlyTypeProxy(String listItem) {
            super(List.class.getSimpleName());
            trieList = new TrieList(
                    OnTheFlyTypeProxy.class.getName(),
                    List.class.getName(),
                    null,
                    listItem);
        }

        @Override
        public Trie resolve(TypeAndTries typeAndTries) {
            return trieList;
        }

        @Override
        public String toSimpleName(TypeAndTries typeAndTries) {
            return trieList.toSimpleName(typeAndTries);
        }
    }
}
