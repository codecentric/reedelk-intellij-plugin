package com.reedelk.plugin.commons;

import com.reedelk.module.descriptor.model.property.PrimitiveDescriptor;
import com.reedelk.module.descriptor.model.property.PropertyDescriptor;
import com.reedelk.runtime.api.annotation.InitValue;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InitPropertyValueTest {

    @Test
    void shouldUseDefaultValueWhenInitIsNotSpecified() {
        // Given
        PrimitiveDescriptor typeDescriptor = new PrimitiveDescriptor();
        typeDescriptor.setType(long.class);

        PropertyDescriptor descriptor = PropertyDescriptor.builder()
                .name("property1")
                .type(typeDescriptor)
                .build();

        // When
        Object actual = InitPropertyValue.of(descriptor);

        // Then
        assertThat(actual).isEqualTo(0L);
    }

    @Test
    void shouldUseDefaultValueWhenInitIsDefault_USE_DEFAULT_VALUE() {
        // Given
        PrimitiveDescriptor typeDescriptor = new PrimitiveDescriptor();
        typeDescriptor.setType(int.class);

        PropertyDescriptor descriptor = PropertyDescriptor.builder()
                .name("property1")
                .initValue(InitValue.USE_DEFAULT_VALUE)
                .type(typeDescriptor)
                .build();

        // When
        Object actual = InitPropertyValue.of(descriptor);

        // Then
        assertThat(actual).isEqualTo(0);
    }

    @Test
    void shouldConvertValueFromInitValueString() {
        // Given
        PrimitiveDescriptor typeDescriptor = new PrimitiveDescriptor();
        typeDescriptor.setType(long.class);

        PropertyDescriptor descriptor = PropertyDescriptor.builder()
                .name("property1")
                .type(typeDescriptor)
                .initValue("98881")
                .build();

        // When
        Object actual = InitPropertyValue.of(descriptor);

        // Then
        assertThat(actual).isEqualTo(98881L);
    }
}
