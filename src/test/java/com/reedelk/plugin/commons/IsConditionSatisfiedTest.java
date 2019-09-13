package com.reedelk.plugin.commons;

import com.reedelk.runtime.api.annotation.When;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.reedelk.plugin.component.domain.TypeObjectDescriptor.TypeObject;
import static org.assertj.core.api.Assertions.assertThat;

class IsConditionSatisfiedTest {

    @Nested
    @DisplayName("Condition is NULL")
    class Null {

        @Test
        void shouldReturnTrueWhenWantedIsNullAndActualIsNull() {
            // Given
            String wantedPropertyValue = When.NULL;
            Object actualPropertyValue = null;

            // When
            boolean actual = IsConditionSatisfied.of(wantedPropertyValue, actualPropertyValue);

            // Then
            assertThat(actual).isTrue();
        }

        @Test
        void shouldReturnFalseWhenWantedIsNullAndActualIsNotNull() {
            // Given
            String wantedPropertyValue = When.NULL;
            Object actualPropertyValue = "a not null object";

            // When
            boolean actual = IsConditionSatisfied.of(wantedPropertyValue, actualPropertyValue);

            // Then
            assertThat(actual).isFalse();
        }

        @Test
        void shouldReturnFalseWhenWantedIsNullAndActualIsTypeObject() {
            // Given
            String wantedPropertyValue = When.NULL;
            Object actualPropertyValue = new TypeObject();

            // When
            boolean actual = IsConditionSatisfied.of(wantedPropertyValue, actualPropertyValue);

            // Then
            assertThat(actual).isFalse();
        }

        @Test
        void shouldReturnTrueWhenWantedIsNullAndActualTypeObjectPropertyIsNotPresent() {
            // Given
            String wantedPropertyValue = "{'myProperty': '" + When.NULL + "'}";
            Object actualPropertyValue = new TypeObject();

            // When
            boolean actual = IsConditionSatisfied.of(wantedPropertyValue, actualPropertyValue);

            // Then
            assertThat(actual).isTrue();
        }

        @Test
        void shouldReturnTrueWhenWantedIsNullAndActualTypeObjectPropertyIsNull() {
            // Given
            String wantedPropertyValue = "{'myProperty': '" + When.NULL + "'}";
            TypeObject actualPropertyValue = new TypeObject();
            actualPropertyValue.set("myProperty", null);

            // When
            boolean actual = IsConditionSatisfied.of(wantedPropertyValue, actualPropertyValue);

            // Then
            assertThat(actual).isTrue();
        }

        @Test
        void shouldReturnFalseWhenWantedIsNullAndActualTypeObjectPropertyIsNotNull() {
            // Given
            String wantedPropertyValue = "{'myProperty': '" + When.NULL + "'}";
            TypeObject actualPropertyValue = new TypeObject();
            actualPropertyValue.set("myProperty", "my value");

            // When
            boolean actual = IsConditionSatisfied.of(wantedPropertyValue, actualPropertyValue);

            // Then
            assertThat(actual).isFalse();
        }

        @Test
        void shouldReturnFalseWhenTestingTypeObjectPropertyButIsNull() {
            // Given
            String wantedPropertyValue = "{'myProperty': '" + When.NULL + "'}";
            TypeObject actualPropertyValue = null;

            // When
            boolean actual = IsConditionSatisfied.of(wantedPropertyValue, actualPropertyValue);

            // Then
            assertThat(actual).isFalse();
        }
    }

    @Nested
    @DisplayName("Condition is BLANK")
    class Blank {

        @Test
        void shouldReturnTrueWhenWantedIsPropertyWithBlankValueAndActualIsTypeObjectWithEmptyString() {
            // Given
            String wantedPropertyValue = "{'configRef': '" + When.BLANK + "'}";
            TypeObject actualPropertyValue = new TypeObject();
            actualPropertyValue.set("configRef", "");

            // When
            boolean actual = IsConditionSatisfied.of(wantedPropertyValue, actualPropertyValue);

            // Then
            assertThat(actual).isTrue();
        }

        @Test
        void shouldReturnTrueWhenWantedIsPropertyWithBlankValueAndActualIsTypeObjectWithNullString() {
            // Given
            String wantedPropertyValue = "{'configRef': '" + When.BLANK + "'}";
            TypeObject actualPropertyValue = new TypeObject();
            actualPropertyValue.set("configRef", null);

            // When
            boolean actual = IsConditionSatisfied.of(wantedPropertyValue, actualPropertyValue);

            // Then
            assertThat(actual).isTrue();
        }

        @Test
        void shouldReturnTrueWhenWantedIsPropertyWithBlankValueAndActualIsTypeObjectWithoutProperty() {
            // Given
            String wantedPropertyValue = "{'configRef': '" + When.BLANK + "'}";
            TypeObject actualPropertyValue = new TypeObject();

            // When
            boolean actual = IsConditionSatisfied.of(wantedPropertyValue, actualPropertyValue);

            // Then
            assertThat(actual).isTrue();
        }

        @Test
        void shouldReturnFalseWhenWantedIsPropertyWithBlankValueAndActualIsTypeObjectWithNotEmptyStringProperty() {
            // Given
            String wantedPropertyValue = "{'configRef': '" + When.BLANK + "'}";
            TypeObject actualPropertyValue = new TypeObject();
            actualPropertyValue.set("configRef", "aabbccdd");

            // When
            boolean actual = IsConditionSatisfied.of(wantedPropertyValue, actualPropertyValue);

            // Then
            assertThat(actual).isFalse();
        }

        @Test
        void shouldReturnFalseWhenWantedIsBlankAndActualIsNotEmptyString() {
            // Given
            String wantedPropertyValue = When.BLANK;
            String actualPropertyValue = "test";

            // When
            boolean actual = IsConditionSatisfied.of(wantedPropertyValue, actualPropertyValue);

            // Then
            assertThat(actual).isFalse();
        }

        @Test
        void shouldReturnTrueWhenWantedIsBlankAndActualIsEmptyString() {
            // Given
            String wantedPropertyValue = When.BLANK;
            String actualPropertyValue = "  ";

            // When
            boolean actual = IsConditionSatisfied.of(wantedPropertyValue, actualPropertyValue);

            // Then
            assertThat(actual).isTrue();
        }

        @Test
        void shouldReturnTrueWhenWantedIsBlankAndActualIsNullString() {
            // Given
            String wantedPropertyValue = When.BLANK;
            String actualPropertyValue = null;

            // When
            boolean actual = IsConditionSatisfied.of(wantedPropertyValue, actualPropertyValue);

            // Then
            assertThat(actual).isTrue();
        }
    }

    @Nested
    @DisplayName("Condition is NOT BLANK")
    class NotBlank {

        @Test
        void shouldReturnFalseWhenWantedIsPropertyWithNotBlankValueAndActualIsTypeObjectWithEmptyString() {
            // Given
            String wantedPropertyValue = "{'configRef': '" + When.NOT_BLANK + "'}";
            TypeObject actualPropertyValue = new TypeObject();
            actualPropertyValue.set("configRef", "");

            // When
            boolean actual = IsConditionSatisfied.of(wantedPropertyValue, actualPropertyValue);

            // Then
            assertThat(actual).isFalse();
        }

        @Test
        void shouldReturnFalseWhenWantedIsPropertyWithNotBlankValueAndActualIsTypeObjectWithNullString() {
            // Given
            String wantedPropertyValue = "{'configRef': '" + When.NOT_BLANK + "'}";
            TypeObject actualPropertyValue = new TypeObject();
            actualPropertyValue.set("configRef", null);

            // When
            boolean actual = IsConditionSatisfied.of(wantedPropertyValue, actualPropertyValue);

            // Then
            assertThat(actual).isFalse();
        }

        @Test
        void shouldReturnFalseWhenWantedIsPropertyWithNotBlankValueAndActualIsTypeObjectWithoutProperty() {
            // Given
            String wantedPropertyValue = "{'configRef': '" + When.NOT_BLANK + "'}";
            TypeObject actualPropertyValue = new TypeObject();

            // When
            boolean actual = IsConditionSatisfied.of(wantedPropertyValue, actualPropertyValue);

            // Then
            assertThat(actual).isFalse();
        }

        @Test
        void shouldReturnTrueWhenWantedIsPropertyWithNotBlankValueAndActualIsTypeObjectWithNotEmptyStringProperty() {
            // Given
            String wantedPropertyValue = "{'configRef': '" + When.NOT_BLANK + "'}";
            TypeObject actualPropertyValue = new TypeObject();
            actualPropertyValue.set("configRef", "aabbccdd");

            // When
            boolean actual = IsConditionSatisfied.of(wantedPropertyValue, actualPropertyValue);

            // Then
            assertThat(actual).isTrue();
        }

        @Test
        void shouldReturnTrueWhenWantedIsNotBlankAndActualIsNotEmptyString() {
            // Given
            String wantedPropertyValue = When.NOT_BLANK;
            String actualPropertyValue = "test";

            // When
            boolean actual = IsConditionSatisfied.of(wantedPropertyValue, actualPropertyValue);

            // Then
            assertThat(actual).isTrue();
        }

        @Test
        void shouldReturnFalseWhenWantedIsNotBlankAndActualIsEmptyString() {
            // Given
            String wantedPropertyValue = When.NOT_BLANK;
            String actualPropertyValue = "  ";

            // When
            boolean actual = IsConditionSatisfied.of(wantedPropertyValue, actualPropertyValue);

            // Then
            assertThat(actual).isFalse();
        }

        @Test
        void shouldReturnFalseWhenWantedIsNotBlankAndActualIsNullString() {
            // Given
            String wantedPropertyValue = When.NOT_BLANK;
            String actualPropertyValue = null;

            // When
            boolean actual = IsConditionSatisfied.of(wantedPropertyValue, actualPropertyValue);

            // Then
            assertThat(actual).isFalse();
        }
    }

    @Nested
    @DisplayName("Condition is compare value")
    class CompareValue {

        @Test
        void shouldReturnTrueWhenWantedIsEqualToStringValue() {
            // Given
            String wantedPropertyValue = "HTTPS";
            String actualPropertyValue = "HTTPS";

            // When
            boolean actual = IsConditionSatisfied.of(wantedPropertyValue, actualPropertyValue);

            // Then
            assertThat(actual).isTrue();
        }

        @Test
        void shouldReturnFalseWhenWantedIsNotEqualToStringValue() {
            // Given
            String wantedPropertyValue = "HTTPS";
            String actualPropertyValue = "HTTP";

            // When
            boolean actual = IsConditionSatisfied.of(wantedPropertyValue, actualPropertyValue);

            // Then
            assertThat(actual).isFalse();
        }

        @Test
        void shouldReturnTrueWhenWantedIsEqualToIntValue() {
            // Given
            String wantedPropertyValue = "1";
            int actualPropertyValue = 1;

            // When
            boolean actual = IsConditionSatisfied.of(wantedPropertyValue, actualPropertyValue);

            // Then
            assertThat(actual).isTrue();
        }

        @Test
        void shouldReturnFalseWhenWantedIsNotEqualToIntValue() {
            // Given
            String wantedPropertyValue = "1";
            int actualPropertyValue = 3;

            // When
            boolean actual = IsConditionSatisfied.of(wantedPropertyValue, actualPropertyValue);

            // Then
            assertThat(actual).isFalse();
        }
    }
}