package com.reedelk.plugin.converter;

import org.json.JSONObject;

public class StringConverter implements ValueConverter<String> {

    @Override
    public String toText(Object value) {
        return (String) value;
    }

    @Override
    public String from(String value) {
        return value;
    }

    @Override
    public String from(String propertyName, JSONObject object) {
        return object.isNull(propertyName) ?
                null :
                object.getString(propertyName);
    }
}