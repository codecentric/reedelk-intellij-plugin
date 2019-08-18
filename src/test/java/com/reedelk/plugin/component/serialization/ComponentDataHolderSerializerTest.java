package com.reedelk.plugin.component.serialization;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ComponentDataHolderSerializerTest {

    @Test
    void shouldDoesNotContainOnlyImplementorPropertyReturnTrue() {
        // Given
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("implementor", "com.reedelk.test.Component");
        jsonObject.put("property1", "one");

        // When
        boolean actual = ComponentDataHolderSerializer
                .doesNotContainOnlyImplementorProperty(jsonObject);

        // Then
        assertThat(actual).isTrue();
    }

    @Test
    void shouldDoesNotContainOnlyImplementorPropertyReturnFalse() {
        // Given
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("implementor", "com.reedelk.test.Component");

        // When
        boolean actual = ComponentDataHolderSerializer
                .doesNotContainOnlyImplementorProperty(jsonObject);

        // Then
        assertThat(actual).isFalse();
    }

    @Test
    void shouldIsNotEmptyReturnTrue() {
        // Given
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("property1", "myProperty value");

        // When
        boolean actual = ComponentDataHolderSerializer
                .isNotEmpty(jsonObject);

        // Then
        assertThat(actual).isTrue();
    }

    @Test
    void shouldIsNotEmptyReturnFalse() {
        // Given
        JSONObject jsonObject = new JSONObject();

        // When
        boolean actual = ComponentDataHolderSerializer
                .isNotEmpty(jsonObject);

        // Then
        assertThat(actual).isFalse();
    }
}