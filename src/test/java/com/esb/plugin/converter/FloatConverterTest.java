package com.esb.plugin.converter;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FloatConverterTest {

    private ValueConverter<Float> converter = new FloatConverter();

    @Test
    void toTextWhenNumericValueIsGiven() {
        // Given
        Float aValue = 2883.231f;

        // When
        String givenValue = converter.toText(aValue);

        // Then
        String expectedValue = "2883.231";
        assertThat(givenValue).isEqualTo(expectedValue);
    }

    @Test
    void toTextWhenNullIsGivenShouldReturnNull() {
        // Given
        Float aValue = null;

        // When
        String actualValue = converter.toText(aValue);

        // Then
        assertThat(actualValue).isNull();
    }

    @Test
    void shouldCorrectlyReturnFloatFromStringValue() {
        // Given
        String aValue = "24211.123";

        // When
        Float actualValue = converter.from(aValue);

        // Then
        Float expectedValue = 24211.123f;
        assertThat(actualValue).isEqualTo(expectedValue);
    }

    @Test
    void shouldReturnDefaultValueWhenStringIsNotParsable() {
        // Given
        String aValue = "aabbcc";

        // When
        Float actualValue = converter.from(aValue);

        // Then
        assertThat(actualValue).isNull();
    }

    @Test
    void shouldReturnNullValueFromJsonObject() {
        // Given
        JSONObject object = new JSONObject();
        object.put("aNumber", JSONObject.NULL);

        // When
        Float actualValue = converter.from("aNumber", object);

        // Then
        assertThat(actualValue).isNull();
    }

    @Test
    void shouldReturnFloatValueFromJsonObject() {
        // Given
        Float aValue = 2111.24f;
        JSONObject object = new JSONObject();
        object.put("aNumber", aValue);

        // When
        Float actualValue = converter.from("aNumber", object);

        // Then
        Float expectedValue = 2111.24f;
        assertThat(actualValue).isEqualTo(expectedValue);
    }

    @Test
    void shouldReturnDefaultValueFromJsonObjectIfPropertyNotPresent() {
        // Given
        JSONObject object = new JSONObject();

        // When
        Float actualValue = converter.from("aNumber", object);

        // Then
        assertThat(actualValue).isNull();
    }
}
