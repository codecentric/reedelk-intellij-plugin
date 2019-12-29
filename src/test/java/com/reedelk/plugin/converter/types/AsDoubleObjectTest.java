package com.reedelk.plugin.converter.types;

import com.reedelk.plugin.converter.ValueConverter;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AsDoubleObjectTest {

    private ValueConverter<Double> converter = new AsDoubleObject();

    @Test
    void toTextWhenNumericValueIsGiven() {
        // Given
        Double aValue = 234.324d;

        // When
        String givenValue = converter.toText(aValue);

        // Then
        String expectedValue = "234.324";
        assertThat(givenValue).isEqualTo(expectedValue);
    }

    @Test
    void toTextWhenNullIsGivenShouldReturnNull() {
        // Given
        Double aValue = null;

        // When
        String actualValue = converter.toText(aValue);

        // Then
        assertThat(actualValue).isNull();
    }

    @Test
    void shouldCorrectlyReturnDoubleFromStringValue() {
        // Given
        String aValue = "234.23412";

        // When
        Double actualValue = converter.from(aValue);

        // Then
        Double expectedValue = 234.23412;
        assertThat(actualValue).isEqualTo(expectedValue);
    }

    @Test
    void shouldReturnDefaultValueNullWhenStringIsNotParsable() {
        // Given
        String aValue = "aabbcc";

        // When
        Double actualValue = converter.from(aValue);

        // Then
        assertThat(actualValue).isNull();
    }

    @Test
    void shouldReturnNullValueFromJsonObject() {
        // Given
        JSONObject object = new JSONObject();
        object.put("aNumber", JSONObject.NULL);

        // When
        Double actualValue = converter.from("aNumber", object);

        // Then
        assertThat(actualValue).isNull();
    }

    @Test
    void shouldReturnDoubleValueFromJsonObject() {
        // Given
        Double aValue = 234912.23d;
        JSONObject object = new JSONObject();
        object.put("aNumber", aValue);

        // When
        Double actualValue = converter.from("aNumber", object);

        // Then
        Double expectedValue = 234912.23d;
        assertThat(actualValue).isEqualTo(expectedValue);
    }

    @Test
    void shouldReturnDefaultValueFromJsonObjectIfPropertyNotPresent() {
        // Given
        JSONObject object = new JSONObject();

        // When
        Double actualValue = converter.from("aNumber", object);

        // Then
        assertThat(actualValue).isNull();
    }
}
