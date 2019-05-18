package com.esb.plugin.converter;

import org.json.JSONObject;

public class BooleanConverter implements ValueConverter<Boolean> {

    @Override
    public String toText(Object value) {
        Boolean realValue = (Boolean) value;
        return Boolean.toString(realValue);
    }

    @Override
    public Boolean from(String value) {
        return Boolean.parseBoolean(value);
    }

    @Override
    public Boolean from(String propertyName, JSONObject object) {
        return object.getBoolean(propertyName);
    }

}
