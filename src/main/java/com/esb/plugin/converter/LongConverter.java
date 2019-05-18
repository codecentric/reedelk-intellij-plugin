package com.esb.plugin.converter;

import com.google.common.base.Defaults;
import org.json.JSONObject;

public class LongConverter implements ValueConverter<Long> {

    @Override
    public String toText(Object value) {
        Long realValue = (Long) value;
        return realValue == null ?
                null :
                Long.toString(realValue);

    }

    @Override
    public Long from(String value) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return Defaults.defaultValue(Long.class);
        }
    }

    @Override
    public Long from(String propertyName, JSONObject object) {
        return object.isNull(propertyName) ?
                null :
                object.getLong(propertyName);
    }
}
