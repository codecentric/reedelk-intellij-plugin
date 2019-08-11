package com.reedelk.plugin.converter;

import org.json.JSONObject;
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
    void toTextWhenNullIsGivenShouldReturnNull() {
        // Given
        Integer aValue = null;

        // When
        String actualValue = converter.toText(aValue);

        // Then
        assertThat(actualValue).isNull();
    }

    @Test
    void shouldCorrectlyReturnIntegerFromStringValue() {
        // Given
        String aValue = "432";

        // When
        Integer actualValue = converter.from(aValue);

        // Then
        Integer expectedValue = 432;
        assertThat(actualValue).isEqualTo(expectedValue);
    }

    @Test
    void shouldReturnDefaultValueNullWhenStringIsNotParsable() {
        // Given
        String aValue = "aabbcc";

        // When
        Integer actualValue = converter.from(aValue);

        // Then
        assertThat(actualValue).isNull();
    }

    @Test
    void shouldReturnNullValueFromJsonObject() {
        // Given
        JSONObject object = new JSONObject();
        object.put("aNumber", JSONObject.NULL);

        // When
        Integer actualValue = converter.from("aNumber", object);

        // Then
        assertThat(actualValue).isNull();
    }

    @Test
    void shouldReturnIntegerValueFromJsonObject() {
        // Given
        Integer aValue = 234;
        JSONObject object = new JSONObject();
        object.put("aNumber", aValue);

        // When
        Integer actualValue = converter.from("aNumber", object);

        // Then
        Integer expectedValue = 234;
        assertThat(actualValue).isEqualTo(expectedValue);
    }

    @Test
    void shouldReturnDefaultValueFromJsonObjectIfPropertyNotPresent() {
        // Given
        JSONObject object = new JSONObject();

        // When
        Integer actualValue = converter.from("aNumber", object);

        // Then
        assertThat(actualValue).isNull();
    }

}