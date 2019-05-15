package com.esb.plugin.converter;

import org.json.JSONObject;

public class BooleanConverter implements PropertyValueConverter<Boolean> {

    @Override
    public String to(Object value) {
        Boolean realValue = (Boolean) value;
        return Boolean.toString(realValue);
    }

    @Override
    public Boolean from(String value) {
        return Boolean.parseBoolean(value);
    }

    @Override
    public Boolean from(String key, JSONObject object) {
        return object.getBoolean(key);
    }

}