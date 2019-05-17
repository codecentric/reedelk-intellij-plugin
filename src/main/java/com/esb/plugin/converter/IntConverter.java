package com.esb.plugin.converter;

import org.json.JSONObject;

public class IntConverter implements ValueConverter<Integer> {

    @Override
    public String toString(Object value) {
        Integer realValue = (Integer) value;
        return Integer.toString(realValue);
    }

    @Override
    public Integer from(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    @Override
    public Integer from(String key, JSONObject object) {
        return object.getInt(key);
    }

}
