package com.esb.plugin.converter;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class IntegerConverterTest {

    private ValueConverter<Integer> converter = new IntegerConverter();

    @Test
    void toTextWhenNumericValueIsGiven() {
        // Given
        Integer aValue = 23;

        // When
        String givenValue = converter.toText(aValue);

        // Then
        String expectedValue = "23";
        assertThat(givenValue).isEqualTo(expectedValue);
    }

    @Test
    void toTextWhenNullIsGivenShouldReturnEmptyString() {
        // Given
        Integer aValue = null;

        // When
        String givenValue = converter.toText(aValue);

        // Then
        String expectedValue = StringUtils.EMPTY;
        assertThat(givenValue).isEqualTo(expectedValue);
    }

}