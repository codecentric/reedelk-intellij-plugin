package com.reedelk.plugin.converter;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class BigDecimalConverterTest {

    private ValueConverter<BigDecimal> converter = new BigDecimalConverter();

    @Test
    void toTextWhenNumericValueIsGiven() {
        // Given
        BigDecimal aValue = new BigDecimal("23.324234");

        // When
        String givenValue = converter.toText(aValue);

        // Then
        String expectedValue = "23.324234";
        assertThat(givenValue).isEqualTo(expectedValue);
    }

    @Test
    void toTextWhenNullIsGivenShouldReturnNull() {
        // Given
        BigDecimal aValue = null;

        // When
        String actualValue = converter.toText(aValue);

        // Then
        assertThat(actualValue).isNull();
    }

    @Test
    void shouldCorrectlyReturnBigDecimalFromStringValue() {
        // Given
        String aValue = "98765.24";

        // When
        BigDecimal actualValue = converter.from(aValue);

        // Then
        BigDecimal expectedValue = new BigDecimal("98765.24");
        assertThat(actualValue).isEqualTo(expectedValue);
    }

    @Test
    void shouldReturnDefaultValueNullWhenStringIsNotParsable() {
        // Given
        String aValue = "aabbcc";

        // When
        BigDecimal actualValue = converter.from(aValue);

        // Then
        assertThat(actualValue).isNull();
    }

    @Test
    void shouldReturnNullValueFromJsonObject() {
        // Given
        JSONObject object = new JSONObject();
        object.put("aNumber", JSONObject.NULL);

        // When
        BigDecimal actualValue = converter.from("aNumber", object);

        // Then
        assertThat(actualValue).isNull();
    }

    @Test
    void shouldReturnBigDecimalValueFromJsonObject() {
        // Given
        BigDecimal aValue = new BigDecimal("234.1231");
        JSONObject object = new JSONObject();
        object.put("aNumber", aValue);

        // When
        BigDecimal actualValue = converter.from("aNumber", object);

        // Then
        BigDecimal expectedValue = new BigDecimal("234.1231");
        assertThat(actualValue).isEqualTo(expectedValue);
    }

    @Test
    void shouldReturnDefaultValueFromJsonObjectIfPropertyNotPresent() {
        // Given
        JSONObject object = new JSONObject();

        // When
        BigDecimal actualValue = converter.from("aNumber", object);

        // Then
        assertThat(actualValue).isNull();
    }

}
