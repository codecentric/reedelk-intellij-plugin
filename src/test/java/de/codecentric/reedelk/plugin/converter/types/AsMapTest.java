package de.codecentric.reedelk.plugin.converter.types;

import de.codecentric.reedelk.plugin.converter.ValueConverter;
import de.codecentric.reedelk.plugin.converter.types.AsMap;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class AsMapTest {

    private ValueConverter<Map<String,Object>> converter = new AsMap();

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
        Map<String, Object> actualValue = converter.from(aValue);

        // Then
        assertThat(actualValue).containsKeys("default1", "default2");
        assertThat(actualValue).containsEntry("default1", "value1");
        assertThat(actualValue).containsEntry("default2", 3);
    }

    @Test
    void shouldReturnDefaultValueEmptyMapWhenDefaultJsonMapIsNotParsable() {
        // Given
        String aValue = "NotValidJson}";

        // When
        Map<String, ?> actualValue = converter.from(aValue);

        // Then
        assertThat(actualValue).isEmpty();
    }

    @Test
    void shouldReturnDefaultValueEmptyMapWhenDefaultJsonIsNull() {
        // Given
        String aValue = null;

        // When
        Map<String, ?> actualValue = converter.from(aValue);

        // Then
        assertThat(actualValue).isEmpty();
    }

    @Test
    void shouldReturnMapFromJsonObject() {
        // Given
        String mapAsJson = "{" +
                "\"key1\":\"value1\"," +
                "\"key2\": 3," +
                "\"key3\": true" +
                "}";
        JSONObject map = new JSONObject(mapAsJson);
        JSONObject object = new JSONObject();
        object.put("headers", map);

        // When
        Map<String, Object> headersMap = converter.from("headers", object);

        // Then
        assertThat(headersMap).containsKeys("key1", "key2", "key3");
        assertThat(headersMap).containsEntry("key1", "value1");
        assertThat(headersMap).containsEntry("key2", 3);
        assertThat(headersMap).containsEntry("key3", true);
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
        assertThat(headersMap).isEmpty();
    }

    @Test
    void shouldReturnEmptyMapFromPropertyNotPresentInJsonObject() {
        // Given
        JSONObject object = new JSONObject();

        // When
        Map<String, ?> headersMap = converter.from("headers", object);

        // Then
        assertThat(headersMap).isEmpty();
    }
}
