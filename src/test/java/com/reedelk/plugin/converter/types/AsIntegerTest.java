package com.reedelk.plugin.converter.types;

import com.reedelk.plugin.converter.ValueConverter;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AsIntegerTest {

    private ValueConverter<Integer> converter = new AsInteger();

    @Test
    void shouldConvertToString() {
        // Given
        int given = 5432;

        // When
        String actual = converter.toText(given);

        // Then
        assertThat(actual).isEqualTo("5432");
    }

    @Test
    void shouldConvertParsedValue() {
        // Given
        String given = "123";

        // When
        int actualValue = converter.from(given);

        // Then
        assertThat(actualValue).isEqualTo(123);
    }

    @Test
    void shouldConvertDefaultValueWhenNumberNotParsable() {
        // Given
        String given = "aabbcc";

        // When
        int actualValue = converter.from(given);

        // Then
        assertThat(actualValue).isEqualTo(0);
    }

    @Test
    void shouldConvertDefaultValueFromNullString() {
        // When
        int actualValue = converter.from(null);

        // Then
        assertThat(actualValue).isEqualTo(0);
    }

    @Test
    void shouldConvertDefaultValueFromJsonObjectIfPropertyNotPresent() {
        // Given
        JSONObject object = new JSONObject();

        // When
        int actualValue = converter.from("aNumber", object);

        // Then
        assertThat(actualValue).isEqualTo(0);
    }

    @Test
    void shouldReturnValueFromObjectProperty() {
        // Given
        JSONObject myObject = new JSONObject();
        myObject.put("property1", 532);

        // When
        Object actual = converter.from("property1", myObject);

        // Then
        assertThat(actual).isEqualTo(532);
    }
}