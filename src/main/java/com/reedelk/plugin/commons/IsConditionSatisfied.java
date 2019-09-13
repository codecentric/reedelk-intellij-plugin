package com.reedelk.plugin.commons;

import com.reedelk.plugin.component.domain.TypeObjectDescriptor;
import com.reedelk.runtime.api.annotation.When;
import com.reedelk.runtime.api.commons.StringUtils;
import org.json.JSONObject;

public class IsConditionSatisfied {


    public static boolean of(String wantedPropertyValue, Object actualPropertyValue) {
        if (When.IS_NULL.equals(wantedPropertyValue)) {
            return actualPropertyValue == null;
        } else if (When.IS_EMPTY.equals(wantedPropertyValue)) {
            return actualPropertyValue instanceof String && StringUtils.isBlank((String) actualPropertyValue);
        } else if (actualPropertyValue instanceof TypeObjectDescriptor.TypeObject) {
            try {
                TypeObjectDescriptor.TypeObject typeObject = (TypeObjectDescriptor.TypeObject) actualPropertyValue;

                boolean matches = true;
                JSONObject object = new JSONObject(wantedPropertyValue);
                for (String key : object.keySet()) {
                    Object expectedValue = object.get(key);
                    if (typeObject.has(key)) {
                        Object actualValue = typeObject.get(key);
                        if (When.IS_EMPTY.equals(expectedValue)) {
                            if (StringUtils.isNotBlank((String) actualValue)) {
                                matches = false;
                                break;
                            }
                        } else {
                            if (expectedValue != actualValue) {
                                matches = false;
                                break;
                            }
                        }
                    } else {
                        matches = false;
                        break;
                    }
                }
                return matches;

            } catch (Exception e) {
                return false;
            }
        } else {
            return actualPropertyValue != null && actualPropertyValue.toString().equals(wantedPropertyValue);
        }
    }
}
