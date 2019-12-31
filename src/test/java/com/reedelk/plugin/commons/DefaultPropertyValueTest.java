package com.reedelk.plugin.commons;

import com.reedelk.module.descriptor.model.ComponentPropertyDescriptor;
import com.reedelk.module.descriptor.model.TypePrimitiveDescriptor;
import com.reedelk.runtime.api.annotation.Default;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultPropertyValueTest {

    @Test
    void shouldUseDefaultValueWhenDefaultIsNotSpecified() {
        // Given
        TypePrimitiveDescriptor typeDescriptor = new TypePrimitiveDescriptor();
        typeDescriptor.setType(long.class);

        ComponentPropertyDescriptor descriptor = ComponentPropertyDescriptor.builder()
                .propertyName("property1")
                .type(typeDescriptor)
                .build();

        // When
        Object actual = DefaultPropertyValue.of(descriptor);

        // Then
        assertThat(actual).isEqualTo(0L);
    }

    @Test
    void shouldUseDefaultValueWhenDefaultIsDefault_USE_DEFAULT_VALUE() {
        // Given
        TypePrimitiveDescriptor typeDescriptor = new TypePrimitiveDescriptor();
        typeDescriptor.setType(int.class);

        ComponentPropertyDescriptor descriptor = ComponentPropertyDescriptor.builder()
                .propertyName("property1")
                .defaultValue(Default.USE_DEFAULT_VALUE)
                .type(typeDescriptor)
                .build();

        // When
        Object actual = DefaultPropertyValue.of(descriptor);

        // Then
        assertThat(actual).isEqualTo(0);
    }

    @Test
    void shouldConvertValueFromDefaultValueString() {
        // Given
        TypePrimitiveDescriptor typeDescriptor = new TypePrimitiveDescriptor();
        typeDescriptor.setType(long.class);

        ComponentPropertyDescriptor descriptor = ComponentPropertyDescriptor.builder()
                .propertyName("property1")
                .type(typeDescriptor)
                .defaultValue("98881")
                .build();

        // When
        Object actual = DefaultPropertyValue.of(descriptor);

        // Then
        assertThat(actual).isEqualTo(98881L);
    }
}