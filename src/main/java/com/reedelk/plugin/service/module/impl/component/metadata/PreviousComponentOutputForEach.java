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
        // TODO: Fixme
        return null;
    }

    @Override
    public String description() {
        // TODO: Fixme
        return null;
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
            String fullyQualifiedType = metadataTypeDTO.getFullyQualifiedType();
            Trie orDefault = typeAndTries.getOrDefault(fullyQualifiedType, Default.UNKNOWN);
            String listItemType = orDefault.listItemType();
            if (StringUtils.isNotBlank(listItemType)) {
                String typeSimpleName = TypeUtils.toSimpleName(listItemType, typeAndTries);
                return Collections.singletonList(new MetadataTypeDTO(typeSimpleName, listItemType, metadataTypeDTO.getProperties()));
            }
        }

        return metadataTypeDTOS;
    }
}
