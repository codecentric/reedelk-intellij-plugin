package com.reedelk.plugin.converter.types;

import com.reedelk.plugin.converter.ValueConverter;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AsListTest {

    private ValueConverter<List<Object>> converter = new AsList();

    @Test
    void toTextShouldThrowExceptionWhenInvoked() {
        // Expect
        UnsupportedOperationException exception =
                Assertions.assertThrows(UnsupportedOperationException.class, () ->
                        converter.toText("anything"), "toText should have thrown exception");

        assertThat(exception).isNotNull();
    }

    @Test
    void shouldReturnCorrectListFromJson() {
        // Given
        String arrayAsJson = "['one','two','three']";

        // When
        List<Object> actualValue = converter.from(arrayAsJson);

        // Then
        assertThat(actualValue).containsExactly("one", "two", "three");
    }

    @Test
    void shouldReturnEmptyListWhenJsonIsNotParsable() {
        // Given
        String aValue = "NotValidJson}";

        // When
        List<Object> actualValue = converter.from(aValue);

        // Then
        assertThat(actualValue).isEmpty();
    }

    @Test
    void shouldReturnEmptyListWhenJsonIsNull() {
        // Given
        String aValue = null;

        // When
        List<Object> actualValue = converter.from(aValue);

        // Then
        assertThat(actualValue).isEmpty();
    }

    @Test
    void shouldReturnListFromJsonObject() {
        // Given
        String arrayAsJson = "['one','two','three']";
        JSONArray jsonArray = new JSONArray(arrayAsJson);
        JSONObject object = new JSONObject();
        object.put("values", jsonArray);

        // When
        List<Object> actual = converter.from("values", object);

        // Then
        assertThat(actual).containsExactly("one", "two", "three");
    }

    @Test
    void shouldReturnEmptyListFromNullJsonObject() {
        // Given
        JSONArray nullArray = null;
        JSONObject object = new JSONObject();
        object.put("values", nullArray);

        // When
        List<Object> actual = converter.from("values", object);

        // Then
        assertThat(actual).isEmpty();
    }

    @Test
    void shouldReturnEmptyListFromPropertyNotPresentInJsonObject() {
        // Given
        JSONObject object = new JSONObject();

        // When
        List<Object> actual = converter.from("headers", object);

        // Then
        assertThat(actual).isEmpty();
    }
}
