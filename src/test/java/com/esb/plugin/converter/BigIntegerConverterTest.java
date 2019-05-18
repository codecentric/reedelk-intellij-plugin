package com.esb.plugin.converter;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.assertj.core.api.Assertions.assertThat;

public class BigIntegerConverterTest {

    private ValueConverter<BigInteger> converter = new BigIntegerConverter();

    @Test
    void toTextWhenNumericValueIsGiven() {
        // Given
        BigInteger aValue = BigInteger.valueOf(123421312323L);

        // When
        String givenValue = converter.toText(aValue);

        // Then
        String expectedValue = "123421312323";
        assertThat(givenValue).isEqualTo(expectedValue);
    }

    @Test
    void toTextWhenNullIsGivenShouldReturnNull() {
        // Given
        BigInteger aValue = null;

        // When
        String actualValue = converter.toText(aValue);

        // Then
        assertThat(actualValue).isNull();
    }

    @Test
    void shouldCorrectlyReturnBigIntegerFromStringValue() {
        // Given
        String aValue = "2349234823489234234";

        // When
        BigInteger actualValue = converter.from(aValue);

        // Then
        BigInteger expectedValue = new BigInteger(aValue);
        assertThat(actualValue).isEqualTo(expectedValue);
    }

    @Test
    void shouldReturnDefaultValueNullWhenStringIsNotParsable() {
        // Given
        String aValue = "aabbcc";

        // When
        BigInteger actualValue = converter.from(aValue);

        // Then
        assertThat(actualValue).isNull();
    }

    @Test
    void shouldReturnNullValueFromJsonObject() {
        // Given
        JSONObject object = new JSONObject();
        object.put("aNumber", JSONObject.NULL);

        // When
        BigInteger actualValue = converter.from("aNumber", object);

        // Then
        assertThat(actualValue).isNull();
    }

    @Test
    void shouldReturnBigIntegerValueFromJsonObject() {
        // Given
        BigInteger aValue = new BigInteger("1234123123123");
        JSONObject object = new JSONObject();
        object.put("aNumber", aValue);

        // When
        BigInteger actualValue = converter.from("aNumber", object);

        // Then
        BigInteger expectedValue = new BigInteger("1234123123123");
        assertThat(actualValue).isEqualTo(expectedValue);
    }

    @Test
    void shouldReturnDefaultValueFromJsonObjectIfPropertyNotPresent() {
        // Given
        JSONObject object = new JSONObject();

        // When
        BigInteger actualValue = converter.from("aNumber", object);

        // Then
        assertThat(actualValue).isNull();
    }

}
