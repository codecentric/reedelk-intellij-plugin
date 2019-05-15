package com.esb.plugin.converter;

import org.json.JSONObject;

public class StringConverter implements PropertyValueConverter<String> {

    @Override
    public String to(Object value) {
        return (String) value;
    }

    @Override
    public String from(String value) {
        return value;
    }

    @Override
    public String from(String key, JSONObject object) {
        return object.getString(key);
    }
}