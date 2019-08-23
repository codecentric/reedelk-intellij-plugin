package com.reedelk.plugin.converter;

import com.intellij.openapi.diagnostic.Logger;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

import static java.lang.String.format;

public class MapConverter implements ValueConverter<Map<String, ?>> {

    private static final Logger LOG = Logger.getInstance(MapConverter.class);

    @Override
    public String toText(Object value) {
        // This method is not used because in the table we
        // are using to display map values we are doing
        // a custom mapping between the Map and the Vector
        // model underlying the table.
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<String, ?> from(String value) {
        try {
            JSONObject jsonObject = new JSONObject(value);
            return asMap(jsonObject);
        } catch (Exception e) {
            LOG.warn(format("Could not parse JSON from value [%s]", value), e);
            return null;
        }
    }

    @Override
    public Map<String, ?> from(String propertyName, JSONObject object) {
        JSONObject jsonObject = object.getJSONObject(propertyName);
        return asMap(jsonObject);
    }

    private Map<String, ?> asMap(JSONObject jsonObject) {
        Map<String, Object> map = new LinkedHashMap<>();
        jsonObject.keySet().forEach(key -> map.put(key, jsonObject.get(key)));
        return map;
    }
}
