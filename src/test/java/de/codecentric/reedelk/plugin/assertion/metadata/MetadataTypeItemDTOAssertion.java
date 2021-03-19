package de.codecentric.reedelk.plugin.assertion.metadata;

import de.codecentric.reedelk.plugin.service.module.impl.component.metadata.MetadataTypeItemDTO;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

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

    // We might have multiple display type separated by a comma, therefore we need
    // to compare the expected with the actual without considering the order they appear.
    public MetadataTypeItemDTOAssertion withDisplayType(String expectedDisplayType) {
        Set<String> expectedDisplayTypes = new HashSet<>(Arrays.asList(expectedDisplayType.split(",")));
        Set<String> actualDisplayTypes = new HashSet<>(Arrays.asList(property.displayType.split(",")));
        assertThat(actualDisplayTypes).isEqualTo(expectedDisplayTypes);
        return this;
    }
}
