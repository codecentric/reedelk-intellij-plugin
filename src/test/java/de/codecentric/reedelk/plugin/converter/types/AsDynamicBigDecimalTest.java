package de.codecentric.reedelk.plugin.converter.types;

import de.codecentric.reedelk.plugin.converter.ValueConverter;
import de.codecentric.reedelk.plugin.converter.types.AsDynamicBigDecimal;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class AsDynamicBigDecimalTest {

    private ValueConverter<Object> converter = new AsDynamicBigDecimal();

    @Test
    void shouldCorrectlyConvertToTextScript() {
        // Given
        String given = "#['123']";

        // When
        String actual = converter.toText(given);

        // Then
        assertThat(actual).isEqualTo(given);
    }

    @Test
    void shouldCorrectlyConvertToTextBigDecimalValue() {
        // Given
        BigDecimal given = new BigDecimal("23341");

        // When
        String actual = converter.toText(given);

        // Then
        assertThat(actual).isEqualTo("23341");
    }

    @Test
    void shouldCorrectlyReturnScriptWhenValueFromScript() {
        // Given
        String given = "#['234.23']";

        // When
        Object actual = converter.from(given);

        // Then
        assertThat(actual).isEqualTo("#['234.23']");
    }

    @Test
    void shouldCorrectlyReturnBigDecimalFromString() {
        // Given
        String given = "234.23234409";

        // When
        Object actual = converter.from(given);

        // Then
        assertThat(actual).isEqualTo(new BigDecimal("234.23234409"));
    }

    @Test
    void shouldCorrectlyReturnNullFromNullString() {
        // Given
        String given = null;

        // When
        Object actual = converter.from(given);

        // Then
        assertThat(actual).isNull();
    }
}