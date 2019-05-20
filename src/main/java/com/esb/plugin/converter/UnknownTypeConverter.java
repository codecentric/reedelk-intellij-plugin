package com.esb.plugin.converter;

import org.json.JSONObject;

public class UnknownTypeConverter implements ValueConverter<Object> {

    @Override
    public String toText(Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object from(String value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object from(String propertyName, JSONObject object) {
        return object.get(propertyName);
    }
}
