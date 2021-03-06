package de.codecentric.reedelk.plugin.converter.types;

import de.codecentric.reedelk.plugin.converter.ValueConverter;
import de.codecentric.reedelk.plugin.converter.types.AsBooleanObject;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AsBooleanObjectTest {

    private ValueConverter<Boolean> converter = new AsBooleanObject();

    @Test
    void toTextWhenBooleanValueIsGiven() {
        // Given
        Boolean aValue = true;

        // When
        String givenValue = converter.toText(aValue);

        // Then
        String expectedValue = Boolean.TRUE.toString();
        assertThat(givenValue).isEqualTo(expectedValue);
    }

    @Test
    void toTextWhenNullIsGivenShouldReturnNull() {
        // Given
        Boolean aValue = null;

        // When
        String actualValue = converter.toText(aValue);

        // Then
        assertThat(actualValue).isEqualTo(null);
    }

    @Test
    void shouldCorrectlyReturnBooleanFromStringValue() {
        // Given
        String aValue = "true";

        // When
        Boolean actualValue = converter.from(aValue);

        // Then
        assertThat(actualValue).isTrue();
    }

    @Test
    void shouldReturnDefaultValueFalseWhenStringIsNotParsable() {
        // Given
        String aValue = "aabbcc";

        // When
        Boolean actualValue = converter.from(aValue);

        // Then
        assertThat(actualValue).isFalse();
    }

    @Test
    void shouldReturnNullValueFromJsonObject() {
        // Given
        JSONObject object = new JSONObject();
        object.put("aNumber", JSONObject.NULL);

        // When
        Boolean actualValue = converter.from("aNumber", object);

        // Then
        assertThat(actualValue).isNull();
    }

    @Test
    void shouldReturnTrueObjectBooleanValueFromJsonObject() {
        // Given
        Boolean aValue = Boolean.TRUE;
        JSONObject object = new JSONObject();
        object.put("aNumber", aValue);

        // When
        Boolean actualValue = converter.from("aNumber", object);

        // Then
        assertThat(actualValue).isTrue();
    }


    @Test
    void shouldReturnFalseObjectBooleanValueFromJsonObject() {
        // Given
        Boolean aValue = Boolean.FALSE;
        JSONObject object = new JSONObject();
        object.put("aNumber", aValue);

        // When
        Boolean actualValue = converter.from("aNumber", object);

        // Then
        assertThat(actualValue).isFalse();
    }

    @Test
    void shouldReturnFalseBooleanValueFromJsonObject() {
        // Given
        boolean aValue = false;
        JSONObject object = new JSONObject();
        object.put("aNumber", aValue);

        // When
        Boolean actualValue = converter.from("aNumber", object);

        // Then
        assertThat(actualValue).isFalse();
    }

    @Test
    void shouldReturnTrueBooleanValueFromJsonObject() {
        // Given
        boolean aValue = true;
        JSONObject object = new JSONObject();
        object.put("aNumber", aValue);

        // When
        Boolean actualValue = converter.from("aNumber", object);

        // Then
        assertThat(actualValue).isTrue();
    }

    @Test
    void shouldReturnDefaultValueFromJsonObjectIfPropertyNotPresent() {
        // Given
        JSONObject object = new JSONObject();

        // When
        Boolean actualValue = converter.from("aNumber", object);

        // Then
        assertThat(actualValue).isNull();
    }
}
