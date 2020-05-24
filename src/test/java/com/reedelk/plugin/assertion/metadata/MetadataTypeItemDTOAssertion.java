package com.reedelk.plugin.assertion.metadata;

import com.reedelk.plugin.service.module.impl.component.metadata.MetadataTypeItemDTO;

import static org.assertj.core.api.Assertions.assertThat;

public class MetadataTypeItemDTOAssertion {

    private final MetadataTypeDTOAssertion parent;
    private final MetadataTypeItemDTO property;

    public MetadataTypeItemDTOAssertion(MetadataTypeDTOAssertion parent, MetadataTypeItemDTO property) {
        this.parent = parent;
        this.property = property;
    }

    public MetadataTypeDTOAssertion and() {
        return parent;
    }

    public MetadataTypeItemDTOAssertion withDisplayType(String expectedDisplayType) {
        assertThat(property.displayType).isEqualTo(expectedDisplayType);
        return this;
    }
}
