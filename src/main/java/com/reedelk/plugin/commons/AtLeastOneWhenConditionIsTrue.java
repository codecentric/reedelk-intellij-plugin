package com.reedelk.plugin.commons;

import com.reedelk.plugin.component.domain.WhenDefinition;

import java.util.List;

public class AtLeastOneWhenConditionIsTrue {

    /**
     * At least one must match. Evaluates each when condition and returns
     * true if and only if at least one when condition is satisfied.
     */
    public static boolean of(List<WhenDefinition> whens, ActualPropertyValueProvider provider) {
        return whens.stream().anyMatch(when -> {
            String propertyName = when.getPropertyName();
            String wantedPropertyValue = when.getPropertyValue();
            Object actualPropertyValue = provider.get(propertyName);
            return IsConditionSatisfied.of(wantedPropertyValue, actualPropertyValue);
        });
    }

    public interface ActualPropertyValueProvider {
        Object get(String propertyName);
    }
}
