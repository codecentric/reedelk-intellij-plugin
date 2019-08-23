package com.reedelk.plugin.converter;

import com.reedelk.plugin.assertion.PluginAssertion;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class MapConverterTest {

    private MapConverter converter = new MapConverter();
    private final String MAP_AS_JSON = "{" +
            "\"key1\":\"value1\"," +
            "\"key2\": 3," +
            "\"key3\": true" +
            "}";

    @Test
    void toTextShouldThrowExceptionWhenInvoked() {
        // Expect
        UnsupportedOperationException exception =
                Assertions.assertThrows(UnsupportedOperationException.class, () ->
                        converter.toText("anything"), "toText should have thrown exception");

        assertThat(exception).isNotNull();
    }

    @Test
    void shouldReturnDefaultValueCorrectMap() {
        // Given
        String aValue = "{\"default1\":\"value1\",\"default2\":3}";

        // When
        Map<String, ?> actualValue = converter.from(aValue);

        // Then
        PluginAssertion.assertThat(actualValue)
                .containsOnlyKeys("default1", "default2")
                .hasKeyWithValue("default1", "value1")
                .hasKeyWithValue("default2", 3);
    }

    @Test
    void shouldReturnDefaultValueEmptyMapWhenDefaultJsonMapIsNotParsable() {
        // Given
        String aValue = "NotValidJson}";

        // When
        Map<String, ?> actualValue = converter.from(aValue);

        // Then
        PluginAssertion.assertThat(actualValue)
                .isEmpty();
    }

    @Test
    void shouldReturnDefaultValueEmptyMapWhenDefaultJsonIsNull() {
        // Given
        String aValue = null;

        // When
        Map<String, ?> actualValue = converter.from(aValue);

        // Then
        PluginAssertion.assertThat(actualValue)
                .isEmpty();
    }

    @Test
    void shouldReturnMapFromJsonObject() {
        // Given
        JSONObject map = new JSONObject(MAP_AS_JSON);
        JSONObject object = new JSONObject();
        object.put("headers", map);

        // When
        Map<String, ?> headersMap = converter.from("headers", object);

        // Then
        PluginAssertion.assertThat(headersMap)
                .containsOnlyKeys("key1", "key2", "key3")
                .hasKeyWithValue("key1", "value1")
                .hasKeyWithValue("key2", 3)
                .hasKeyWithValue("key3", true);
    }

    @Test
    void shouldReturnEmptyMapFromNullJsonObject() {
        // Given
        JSONObject nullMap = null;
        JSONObject object = new JSONObject();
        object.put("headers", nullMap);

        // When
        Map<String, ?> headersMap = converter.from("headers", object);

        // Then
        PluginAssertion.assertThat(headersMap)
                .isEmpty();
    }

    @Test
    void shouldReturnEmptyMapFromPropertyNotPresentInJsonObject() {
        // Given
        JSONObject object = new JSONObject();

        // When
        Map<String, ?> headersMap = converter.from("headers", object);

        // Then
        PluginAssertion.assertThat(headersMap)
                .isEmpty();
    }
}