package com.esb.plugin.converter;

import org.json.JSONObject;

public class FloatConverter implements ValueConverter<Float> {

    @Override
    public String asString(Object value) {
        Float realValue = (Float) value;
        return String.valueOf(realValue);
    }

    @Override
    public Float from(String value) {
        try {
            return Float.parseFloat(value);
        } catch (NumberFormatException e) {
            return 0.0f;
        }
    }

    @Override
    public Float from(String key, JSONObject object) {
        return object.getFloat(key);
    }
}
