package com.reedelk.plugin.converter;

import org.json.JSONObject;

import java.util.Map;

public class MapConverter implements ValueConverter<Map<String, ?>> {
    @Override
    public String toText(Object value) {
        return null;
    }

    @Override
    public Map<String, ?> from(String value) {
        return null;
    }

    @Override
    public Map<String, ?> from(String propertyName, JSONObject object) {
        return null;
    }
}
