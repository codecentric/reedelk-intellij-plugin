package com.reedelk.plugin.assertion.metadata;

import com.reedelk.plugin.service.module.impl.component.metadata.MetadataTypeDTO;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class MetadataTypeDTOListAssertion {

    private final List<MetadataTypeDTO> metadataTypeDTOList;

    public MetadataTypeDTOListAssertion(List<MetadataTypeDTO> metadataTypeDTOList) {
        this.metadataTypeDTOList = metadataTypeDTOList;
    }

    public MetadataTypeDTOAssertion contains(String expectedTypeFullyQualifiedName, String ...expectedDisplayName) {
        for (MetadataTypeDTO dto : metadataTypeDTOList) {
            String typeFullyQualifiedName = dto.getTypeProxy().getTypeFullyQualifiedName();
            boolean sameType = expectedTypeFullyQualifiedName.equals(typeFullyQualifiedName);
            boolean sameDisplayType = sameReturnDisplayType(dto.getDisplayType(), expectedDisplayName);
            if (sameType && sameDisplayType) {
                return new MetadataTypeDTOAssertion(dto, this);
            }
        }
        throw new RuntimeException("Could not find metadata type dto with type fully qualified name=[" + expectedTypeFullyQualifiedName + "], display name=[" + Arrays.toString(expectedDisplayName) + "]");
    }

    // There might be multiple return types e.g Type1,Type2 and so on. We want to compare them
    // without explicitly consider the order, e.g Type1,Type2 == Type2,Type1
    private boolean sameReturnDisplayType(String actualReturnTypeDisplayValue, String ...expectedReturnTypeDisplayValues) {
        for (String expectedReturnTypeDisplayValue : expectedReturnTypeDisplayValues) {
            String[] actualReturnDisplayTypeSegments = actualReturnTypeDisplayValue.split(",");
            String[] expectedReturnDisplayTypeSegments = expectedReturnTypeDisplayValue.split(",");
            if (new HashSet<>(Arrays.asList(actualReturnDisplayTypeSegments))
                    .equals(new HashSet<>(Arrays.asList(expectedReturnDisplayTypeSegments)))) {
                return true;
            }
        }
        return false;
    }
}
