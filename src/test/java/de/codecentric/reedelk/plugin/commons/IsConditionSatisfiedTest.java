package de.codecentric.reedelk.plugin.commons;

import de.codecentric.reedelk.plugin.commons.IsConditionSatisfied;
import de.codecentric.reedelk.plugin.commons.TypeObjectFactory;
import de.codecentric.reedelk.module.descriptor.model.property.ObjectDescriptor;
import de.codecentric.reedelk.runtime.api.annotation.When;
import de.codecentric.reedelk.runtime.commons.JsonParser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

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
            Object actualPropertyValue = TypeObjectFactory.newInstance();

            // When
            boolean actual = IsConditionSatisfied.of(wantedPropertyValue, actualPropertyValue);

            // Then
            assertThat(actual).isFalse();
        }

        @Test
        void shouldReturnTrueWhenWantedIsNullAndActualTypeObjectPropertyIsNotPresent() {
            // Given
            String wantedPropertyValue = "{'myProperty': '" + When.NULL + "'}";
            Object actualPropertyValue = TypeObjectFactory.newInstance();

            // When
            boolean actual = IsConditionSatisfied.of(wantedPropertyValue, actualPropertyValue);

            // Then
            assertThat(actual).isTrue();
        }

        @Test
        void shouldReturnTrueWhenWantedIsNullAndActualTypeObjectPropertyIsNull() {
            // Given
            String wantedPropertyValue = "{'myProperty': '" + When.NULL + "'}";
            ObjectDescriptor.TypeObject actualPropertyValue = TypeObjectFactory.newInstance();
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
            ObjectDescriptor.TypeObject actualPropertyValue = TypeObjectFactory.newInstance();
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
            ObjectDescriptor.TypeObject actualPropertyValue = null;

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
            String wantedPropertyValue = "{'ref': '" + When.BLANK + "'}";
            ObjectDescriptor.TypeObject actualPropertyValue = TypeObjectFactory.newInstance();
            actualPropertyValue.set(JsonParser.Component.ref(), "");

            // When
            boolean actual = IsConditionSatisfied.of(wantedPropertyValue, actualPropertyValue);

            // Then
            assertThat(actual).isTrue();
        }

        @Test
        void shouldReturnTrueWhenWantedIsPropertyWithBlankValueAndActualIsTypeObjectWithNullString() {
            // Given
            String wantedPropertyValue = "{'ref': '" + When.BLANK + "'}";
            ObjectDescriptor.TypeObject actualPropertyValue = TypeObjectFactory.newInstance();
            actualPropertyValue.set(JsonParser.Component.ref(), null);

            // When
            boolean actual = IsConditionSatisfied.of(wantedPropertyValue, actualPropertyValue);

            // Then
            assertThat(actual).isTrue();
        }

        @Test
        void shouldReturnTrueWhenWantedIsPropertyWithBlankValueAndActualIsTypeObjectWithoutProperty() {
            // Given
            String wantedPropertyValue = "{'ref': '" + When.BLANK + "'}";
            ObjectDescriptor.TypeObject actualPropertyValue = TypeObjectFactory.newInstance();

            // When
            boolean actual = IsConditionSatisfied.of(wantedPropertyValue, actualPropertyValue);

            // Then
            assertThat(actual).isTrue();
        }

        @Test
        void shouldReturnFalseWhenWantedIsPropertyWithBlankValueAndActualIsTypeObjectWithNotEmptyStringProperty() {
            // Given
            String wantedPropertyValue = "{'ref': '" + When.BLANK + "'}";
            ObjectDescriptor.TypeObject actualPropertyValue = TypeObjectFactory.newInstance();
            actualPropertyValue.set(JsonParser.Component.ref(), "aabbccdd");

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
            String wantedPropertyValue = "{'ref': '" + When.NOT_BLANK + "'}";
            ObjectDescriptor.TypeObject actualPropertyValue = TypeObjectFactory.newInstance();
            actualPropertyValue.set(JsonParser.Component.ref(), "");

            // When
            boolean actual = IsConditionSatisfied.of(wantedPropertyValue, actualPropertyValue);

            // Then
            assertThat(actual).isFalse();
        }

        @Test
        void shouldReturnFalseWhenWantedIsPropertyWithNotBlankValueAndActualIsTypeObjectWithNullString() {
            // Given
            String wantedPropertyValue = "{'ref': '" + When.NOT_BLANK + "'}";
            ObjectDescriptor.TypeObject actualPropertyValue = TypeObjectFactory.newInstance();
            actualPropertyValue.set(JsonParser.Component.ref(), null);

            // When
            boolean actual = IsConditionSatisfied.of(wantedPropertyValue, actualPropertyValue);

            // Then
            assertThat(actual).isFalse();
        }

        @Test
        void shouldReturnFalseWhenWantedIsPropertyWithNotBlankValueAndActualIsTypeObjectWithoutProperty() {
            // Given
            String wantedPropertyValue = "{'ref': '" + When.NOT_BLANK + "'}";
            ObjectDescriptor.TypeObject actualPropertyValue = TypeObjectFactory.newInstance();

            // When
            boolean actual = IsConditionSatisfied.of(wantedPropertyValue, actualPropertyValue);

            // Then
            assertThat(actual).isFalse();
        }

        @Test
        void shouldReturnTrueWhenWantedIsPropertyWithNotBlankValueAndActualIsTypeObjectWithNotEmptyStringProperty() {
            // Given
            String wantedPropertyValue = "{'ref': '" + When.NOT_BLANK + "'}";
            ObjectDescriptor.TypeObject actualPropertyValue = TypeObjectFactory.newInstance();
            actualPropertyValue.set(JsonParser.Component.ref(), "aabbccdd");

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
