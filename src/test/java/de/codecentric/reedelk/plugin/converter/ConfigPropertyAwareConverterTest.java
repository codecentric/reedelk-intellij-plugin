package de.codecentric.reedelk.plugin.converter;

import de.codecentric.reedelk.plugin.converter.ConfigPropertyAwareConverters;
import de.codecentric.reedelk.plugin.converter.ValueConverter;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConfigPropertyAwareConverterTest {

    private ValueConverter<Integer> converter =
            ConfigPropertyAwareConverters.getInstance().forType(int.class);

    @Test
    void shouldConvertToTextCorrectly() {
        // Given
        int given = 542;

        // When
        String actual = converter.toText(given);

        // Then
        assertThat(actual).isEqualTo("542");
    }

    @Test
    void shouldKeepConfigPropertyStringWhenFromContainsConfigPropertyDefinition() {
        // Given
        String given = "${my.host.name}";

        // When
        Object actual = converter.from(given);

        // Then
        assertThat(actual).isEqualTo("${my.host.name}");
    }

    @Test
    void shouldConvertFromStringCorrectly() {
        // Given
        String given = "6432";

        // When
        Object actual = converter.from(given);

        // Then
        assertThat(actual).isEqualTo(6432);
    }

    @Test
    void shouldReturnDefaultValueWhenStringIsNull() {
        // Given
        String given = null;

        // When
        Object actual = converter.from(given);

        // Then
        assertThat(actual).isEqualTo(0);
    }

    @Test
    void shouldReturnDefaultValueWhenStringIsNotParsable() {
        // Given
        String given = "abc";

        // When
        Object actual = converter.from(given);

        // Then
        assertThat(actual).isEqualTo(0);
    }

    @Test
    void shouldReturnConfigPropertyDefinitionFromJsonObject() {
        // Given
        JSONObject myObject = new JSONObject();
        myObject.put("property1","${my.host.name}");

        // When
        Object actual = converter.from("property1", myObject);

        // Then
        assertThat(actual).isEqualTo("${my.host.name}");
    }

    @Test
    void shouldReturnDefaultValueWhenObjectDoesNotContainProperty() {
        // Given
        JSONObject myObject = new JSONObject();
        myObject.put("property2", "Testing");

        // When
        Object actual = converter.from("property1", myObject);

        // Then
        assertThat(actual).isEqualTo(0);
    }

    @Test
    void shouldReturnDefaultValueWhenObjectContainsNullProperty() {
        // Given
        Integer value = null;
        JSONObject myObject = new JSONObject();
        myObject.put("property1", value);

        // When
        Object actual = converter.from("property1", myObject);

        // Then
        assertThat(actual).isEqualTo(0);
    }

    @Test
    void shouldReturnValueFromIntObjectProperty() {
        // Given
        JSONObject myObject = new JSONObject();
        myObject.put("property1", 6532);

        // When
        Object actual = converter.from("property1", myObject);

        // Then
        assertThat(actual).isEqualTo(6532);
    }

    @Test
    void shouldReturnValueFromStringObjectProperty() {
        // Given
        JSONObject myObject = new JSONObject();
        myObject.put("property1", "6532");

        // When
        Object actual = converter.from("property1", myObject);

        // Then
        assertThat(actual).isEqualTo(6532);
    }
}