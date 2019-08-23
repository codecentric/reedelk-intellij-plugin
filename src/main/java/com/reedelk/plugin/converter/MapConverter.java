package com.reedelk.plugin.converter;

import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

public class MapConverter implements ValueConverter<Map<String, ?>> {

    @Override
    public String toText(Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<String, ?> from(String value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<String, ?> from(String propertyName, JSONObject object) {
        JSONObject jsonObject = object.getJSONObject(propertyName);
        Map<String, Object> map = new LinkedHashMap<>();
        jsonObject.keySet().forEach(key -> map.put(key, jsonObject.get(key)));
        return map;
    }
}
