package com.reedelk.plugin.commons;

import com.reedelk.runtime.api.annotation.When;
import com.reedelk.runtime.api.commons.StringUtils;
import org.json.JSONObject;

import static com.reedelk.plugin.component.domain.TypeObjectDescriptor.TypeObject;

public class IsConditionSatisfied {

    public static boolean of(String wantedPropertyValue, Object actualPropertyValue) {
        if (When.NULL.equals(wantedPropertyValue)) {
            return actualPropertyValue == null;
        } else if (When.BLANK.equals(wantedPropertyValue)) {
            return isBlank(actualPropertyValue);
        } else if (actualPropertyValue instanceof TypeObject) {
            return isConditionSatisfied(wantedPropertyValue, (TypeObject) actualPropertyValue);
        } else {
            return actualPropertyValue != null && actualPropertyValue.toString().equals(wantedPropertyValue);
        }
    }

    private static boolean isConditionSatisfied(String wantedPropertyValue, TypeObject actualPropertyValue) {
        try {
            boolean matches = true;
            JSONObject whenObjectDefinition = new JSONObject(wantedPropertyValue);
            for (String key : whenObjectDefinition.keySet()) {
                Object expectedValue = whenObjectDefinition.get(key);
                if (When.PROPERTY_NOT_PRESENT.equals(expectedValue)) {
                    matches = !actualPropertyValue.has(key);
                } else if (When.BLANK.equals(expectedValue)) {
                    matches = whenIsBlank(actualPropertyValue, key);
                }
            }
            return matches;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean whenIsBlank(TypeObject typeObject, String key) {
        if (typeObject.has(key)) {
            Object actualValue = typeObject.get(key);
            return StringUtils.isBlank((String) actualValue);
        } else {
            return false;
        }
    }

    private static boolean isString(Object value) {
        return value instanceof String;
    }

    private static boolean isBlank(Object value) {
        return isString(value) && StringUtils.isBlank((String) value);
    }
}
