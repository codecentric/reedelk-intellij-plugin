package com.esb.plugin.converter;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StringConverterTest {

    private ValueConverter<String> converter = new StringConverter();

    @Test
    void toTextWhenStringValueIsGiven() {
        // Given
        String aValue = "my string value";

        // When
        String givenValue = converter.toText(aValue);

        // Then
        assertThat(givenValue).isEqualTo(aValue);
    }

    @Test
    void toTextWhenNullIsGivenShouldReturnNull() {
        // Given
        String aValue = null;

        // When
        String actualValue = converter.toText(aValue);

        // Then
        assertThat(actualValue).isNull();
    }

    @Test
    void shouldCorrectlyReturnStringFromStringValue() {
        // Given
        String aValue = "my test string";

        // When
        String actualValue = converter.from(aValue);

        // Then
        String expectedValue = "my test string";
        assertThat(actualValue).isEqualTo(expectedValue);
    }

    @Test
    void shouldReturnNullValueFromJsonObject() {
        // Given
        JSONObject object = new JSONObject();
        object.put("aNumber", JSONObject.NULL);

        // When
        String actualValue = converter.from("aNumber", object);

        // Then
        assertThat(actualValue).isNull();
    }

    @Test
    void shouldReturnStringValueFromJsonObject() {
        // Given
        String aValue = "my string";
        JSONObject object = new JSONObject();
        object.put("aNumber", aValue);

        // When
        String actualValue = converter.from("aNumber", object);

        // Then
        String expectedValue = "my string";
        assertThat(actualValue).isEqualTo(expectedValue);
    }

    @Test
    void shouldReturnDefaultValueFromJsonObjectIfPropertyNotPresent() {
        // Given
        JSONObject object = new JSONObject();

        // When
        String actualValue = converter.from("aNumber", object);

        // Then
        assertThat(actualValue).isNull();
    }
}
