package com.reedelk.plugin.assertion.metadata;

import com.reedelk.plugin.service.module.impl.component.metadata.MetadataTypeDTO;
import com.reedelk.plugin.service.module.impl.component.metadata.MetadataTypeItemDTO;

import java.util.Collection;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;

public class MetadataTypeDTOAssertion {

    private final MetadataTypeDTO metadataType;

    public MetadataTypeDTOAssertion(MetadataTypeDTO metadataType) {
        this.metadataType = metadataType;
    }

    public MetadataTypeDTOAssertion hasDisplayType(String expectedDisplayType) {
        assertThat(metadataType.getDisplayType()).isEqualTo(expectedDisplayType);
        return this;
    }

    public MetadataTypeDTOAssertion hasPropertyCount(int expectedPropertiesCount) {
        Collection<MetadataTypeItemDTO> properties = metadataType.getProperties();
        assertThat(properties).hasSize(expectedPropertiesCount);
        return this;
    }

    public MetadataTypeDTOAssertion hasType(String expectedTypeFullyQualifiedName) {
        assertThat(metadataType.getTypeProxy().getTypeFullyQualifiedName())
                .isEqualTo(expectedTypeFullyQualifiedName);
        return this;
    }

    public MetadataTypeDTOAssertion hasNoProperties() {
        Collection<MetadataTypeItemDTO> property = metadataType.getProperties();
        assertThat(property).isEmpty();
        return this;
    }

    public MetadataTypeItemDTOAssertion hasProperty(String expectedPropertyName) {
        Collection<MetadataTypeItemDTO> properties = metadataType.getProperties();
        for (MetadataTypeItemDTO property : properties) {
            if (property.name.equals(expectedPropertyName)) {
                return new MetadataTypeItemDTOAssertion(this, property);
            }
        }
        throw new IllegalArgumentException(
                format("Could not find metadata type property named=[%s]", expectedPropertyName));
    }
}
