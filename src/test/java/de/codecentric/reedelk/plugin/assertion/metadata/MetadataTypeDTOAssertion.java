package de.codecentric.reedelk.plugin.assertion.metadata;

import de.codecentric.reedelk.plugin.service.module.impl.component.metadata.MetadataTypeDTO;
import de.codecentric.reedelk.plugin.service.module.impl.component.metadata.MetadataTypeItemDTO;

import java.util.Arrays;
import java.util.Collection;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class MetadataTypeDTOAssertion {

    private final MetadataTypeDTO metadataType;
    private final MetadataTypeDTOListAssertion parent;

    public MetadataTypeDTOAssertion(MetadataTypeDTO metadataType) {
        this.parent = null;
        this.metadataType = metadataType;
    }

    public MetadataTypeDTOAssertion(MetadataTypeDTO metadataType, MetadataTypeDTOListAssertion parent) {
        this.parent = parent;
        this.metadataType = metadataType;
    }

    public MetadataTypeDTOListAssertion and() {
        return parent;
    }

    public MetadataTypeDTOAssertion hasDisplayType(String expectedDisplayType) {
        assertThat(metadataType.getDisplayType()).isEqualTo(expectedDisplayType);
        return this;
    }

    public MetadataTypeDTOAssertion hasDisplayTypeContainingOneOf(String ...expectedTypes) {
        for (String expectedType : expectedTypes) {
            if (expectedType.equals(metadataType.getDisplayType())) return this;
        }
        fail("Could not find display type containing any of=[" + Arrays.toString(expectedTypes) + "]");
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
