package com.reedelk.plugin.commons;

import com.reedelk.plugin.component.domain.TypeObjectDescriptor;
import com.reedelk.runtime.api.annotation.When;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class IsConditionSatisfiedTest {

    //@When(propertyName = "configuration", propertyValue = When.NULL)
    //@When(propertyName = "configuration", propertyValue = "{'configRef': '" + When.BLANK + "'}")
    //@When(propertyName = "configuration", propertyValue = "{'configRef': '" + When.PROPERTY_NOT_PRESENT + "'}")

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
    void shouldReturnTrueWhenWantedIsBlankAndActualIsTypeObjectWithBlankProperty() {
        // Given
        TypeObjectDescriptor.TypeObject mySharedObject = new TypeObjectDescriptor.TypeObject();

        String wantedPropertyValue = "{'configRef': '" + When.PROPERTY_NOT_PRESENT + "'}";
        Object actualPropertyValue = mySharedObject;

        // When
        boolean actual = IsConditionSatisfied.of(wantedPropertyValue, actualPropertyValue);

        // Then
        assertThat(actual).isTrue();
    }

    @Test
    void shouldReturnTrueWhenWantedIsPropertyNotPresentAndActualIsTypeObjectWithPropertyNotPresent() {
        // Given
        TypeObjectDescriptor.TypeObject mySharedObject = new TypeObjectDescriptor.TypeObject();
        mySharedObject.set("configRef", "");

        String wantedPropertyValue = "{'configRef': '" + When.BLANK + "'}";
        Object actualPropertyValue = mySharedObject;

        // When
        boolean actual = IsConditionSatisfied.of(wantedPropertyValue, actualPropertyValue);

        // Then
        assertThat(actual).isTrue();
    }

}