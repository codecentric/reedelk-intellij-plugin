package com.reedelk.plugin.converter.types;

import com.reedelk.plugin.converter.ValueConverter;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AsLongObjectTest {

    private ValueConverter<Long> converter = new AsLongObject();

    @Test
    void toTextWhenNumericValueIsGiven() {
        // Given
        Long aValue = 8723L;

        // When
        String givenValue = converter.toText(aValue);

        // Then
        String expectedValue = "8723";
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
    void shouldCorrectlyReturnNumberFromStringValue() {
        // Given
        String aValue = "99832";

        // When
        Long actualValue = converter.from(aValue);

        // Then
        Long expectedValue = 99832L;
        assertThat(actualValue).isEqualTo(expectedValue);
    }

    @Test
    void shouldReturnDefaultValueNullWhenStringIsNotParsable() {
        // Given
        String aValue = "ccddeeff";

        // When
        Long actualValue = converter.from(aValue);

        // Then
        assertThat(actualValue).isNull();
    }

    @Test
    void shouldReturnNullValueFromJsonObject() {
        // Given
        JSONObject object = new JSONObject();
        object.put("aNumber", JSONObject.NULL);

        // When
        Long actualValue = converter.from("aNumber");

        // Then
        assertThat(actualValue).isNull();
    }

    @Test
    void shouldReturnLongValueFromJsonObject() {
        // Given
        Long aValue = 234L;
        JSONObject object = new JSONObject();
        object.put("aNumber", aValue);

        // When
        Long actualValue = converter.from("aNumber", object);

        // Then
        Long expectedValue = 234L;
        assertThat(actualValue).isEqualTo(expectedValue);
    }

    @Test
    void shouldReturnDefaultValueFromJsonObjectIfPropertyNotPresent() {
        // Given
        JSONObject object = new JSONObject();

        // When
        Long actualValue = converter.from("aNumber");

        // Then
        assertThat(actualValue).isNull();
    }

}
