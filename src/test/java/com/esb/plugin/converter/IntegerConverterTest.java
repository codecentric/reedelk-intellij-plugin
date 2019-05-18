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
        String actualValue = converter.toText(aValue);

        // Then
        String expectedValue = StringUtils.EMPTY;
        assertThat(actualValue).isEqualTo(expectedValue);
    }

    @Test
    void shouldCorrectlyReturnNumberFromStringValue() {
        // Given
        String aValue = "432";

        // When
        Integer actualValue = converter.from(aValue);

        // Then
        Integer expectedValue = 432;
        assertThat(actualValue).isEqualTo(expectedValue);
    }

    @Test
    void shouldReturnDefaultValueWhenStringIsNotParsable() {
        // Given
        String aValue = "aabbcc";

        // When
        Integer actualValue = converter.from(aValue);

        // Then
        Integer expectedValue = null;
        assertThat(actualValue).isEqualTo(expectedValue);
    }


}