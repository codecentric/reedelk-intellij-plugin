package com.esb.plugin.converter;

import org.json.JSONObject;

public class LongConverter implements PropertyValueConverter<Long> {

    @Override
    public String to(Object value) {
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
        return object.getLong(key);
    }
}
