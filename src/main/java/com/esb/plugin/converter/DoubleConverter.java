package com.esb.plugin.converter;

import org.json.JSONObject;

public class DoubleConverter implements ValueConverter<Double> {

    @Override
    public String asString(Object value) {
        Double realValue = (Double) value;
        return String.valueOf(realValue);
    }

    @Override
    public Double from(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return 0.0d;
        }
    }

    @Override
    public Double from(String key, JSONObject object) {
        return object.getDouble(key);
    }
}
