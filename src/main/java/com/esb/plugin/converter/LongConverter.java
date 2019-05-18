package com.esb.plugin.converter;

import org.json.JSONObject;

public class LongConverter implements ValueConverter<Long> {

    @Override
    public String toText(Object value) {
        Long realValue = (Long) value;
        return String.valueOf(realValue);
    }

    @Override
    public Long from(String value) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return 0L;
        }
    }

    @Override
    public Long from(String key, JSONObject object) {
        if (object.isNull(key)) return 0L;
        return object.getLong(key);
    }
}
