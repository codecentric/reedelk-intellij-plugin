package com.reedelk.plugin.service.module.impl.component.metadata;

import com.reedelk.plugin.service.module.impl.component.completion.*;
import com.reedelk.runtime.api.commons.StringUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class PreviousComponentOutputForEach extends AbstractPreviousComponentOutput {

    private final PreviousComponentOutput previousComponentOutput;

    public PreviousComponentOutputForEach(PreviousComponentOutput previousComponentOutput) {
        this.previousComponentOutput = previousComponentOutput;
    }

    @Override
    public Collection<Suggestion> buildDynamicSuggestions(Suggestion suggestion, TypeAndTries typeAndTrieMap, boolean flatten) {
        return previousComponentOutput.buildDynamicSuggestions(suggestion, typeAndTrieMap, flatten);
    }

    @Override
    public String description() {
        return StringUtils.EMPTY;
    }

    @Override
    public MetadataTypeDTO mapAttributes(CompletionFinder completionFinder, TypeAndTries typeAndTries) {
        return previousComponentOutput.mapAttributes(completionFinder, typeAndTries);
    }

    @Override
    public List<MetadataTypeDTO> mapPayload(CompletionFinder completionFinder, TypeAndTries typeAndTries) {
        List<MetadataTypeDTO> metadataTypeDTOS = previousComponentOutput.mapPayload(completionFinder, typeAndTries);
        if (metadataTypeDTOS.size() == 1) {
            MetadataTypeDTO metadataTypeDTO = metadataTypeDTOS.stream().findFirst().get();
            TypeProxy typeProxy = metadataTypeDTO.getTypeProxy();

            // we extract the items one by one from the for each list.
            // TODO: This is maybe applicable for maps as well!?
            if (typeProxy.isList(typeAndTries)) {
                Trie trie = typeProxy.resolve(typeAndTries);
                String listItemType = trie.listItemType();
                String typeSimpleName = TypeUtils.toSimpleName(listItemType, typeAndTries);
                MetadataTypeDTO unrolledList = new MetadataTypeDTO(typeSimpleName,
                        TypeProxy.create(listItemType),
                        metadataTypeDTO.getProperties());
                return Collections.singletonList(unrolledList);
            }
        }

        return metadataTypeDTOS;
    }
}
