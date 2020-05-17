package com.reedelk.plugin.service.module.impl.component.metadata;

import com.reedelk.plugin.service.module.impl.component.completion.*;

import java.util.*;

import static java.util.stream.Collectors.*;

public class PreviousComponentOutputJoin extends AbstractPreviousComponentOutput {

    private final List<PreviousComponentOutput> outputs;

    public PreviousComponentOutputJoin(List<PreviousComponentOutput> outputs) {
        this.outputs = outputs;
    }

    @Override
    public Collection<Suggestion> buildDynamicSuggestions(Suggestion suggestion, TypeAndTries typeAndTrieMap, boolean flatten) {
        // Output is List<Of all the types>
        List<Suggestion> suggestions = new ArrayList<>();
        outputs.forEach(previousComponentOutput ->
                suggestions.addAll(previousComponentOutput.buildDynamicSuggestions(suggestion, typeAndTrieMap, flatten)));
        return suggestions;
    }

    @Override
    public String description() {
        return "List of multiple payloads";
    }

    @Override
    public MetadataTypeDTO mapAttributes(CompletionFinder completionFinder, TypeAndTries typeAndTries) {
        List<MetadataTypeDTO> attributesToMerge = outputs.stream()
                .map(previousComponentOutput -> previousComponentOutput.mapAttributes(completionFinder, typeAndTries))
                .collect(toList());
        return PreviousComponentOutputDefault.mergeMetadataTypes(attributesToMerge);
    }

    @Override
    public List<MetadataTypeDTO> mapPayload(CompletionFinder completionFinder, TypeAndTries typeAndTries) {
        // The output is a List of types, if the types are all the same
        List<MetadataTypeDTO> allTypes = new ArrayList<>();

        outputs.stream()
                .map(previousComponentOutput -> previousComponentOutput.mapPayload(completionFinder, typeAndTries))
                .forEach(allTypes::addAll);

        // Logic should be if all types have the same type
        Map<String, List<MetadataTypeDTO>> collect = allTypes.stream()
                .collect(groupingBy(MetadataTypeDTO::getFullyQualifiedType));

        if (collect.size() == 1) {
            // All the outputs have the same type
            // Output List<Type> : Type -> Unroll properties...
            // TODO: I need a type resolve which dynamically creates types
            Map.Entry<String, List<MetadataTypeDTO>> next = collect.entrySet().iterator().next();
            String type = next.getValue().get(0).getFullyQualifiedType();
            Trie typeTrie = typeAndTries.getOrDefault(type);
            MetadataTypeDTO sameTypeElementsList = unrollListType(completionFinder, typeAndTries, typeTrie, type);
            return Collections.singletonList(sameTypeElementsList);

        } else {
            // Output List<Type1,Type2,Type3> ... do not unroll the properties
            String listArgs = collect.keySet()
                    .stream()
                    .map(theType -> TypeUtils.toSimpleName(theType, typeAndTries))
                    .collect(joining(","));
            MetadataTypeDTO metadataTypeDTO = new MetadataTypeDTO("List<" + listArgs + ">",
                    List.class.getName(),
                    Collections.emptyList());
            return Collections.singletonList(metadataTypeDTO);
        }
    }
}
