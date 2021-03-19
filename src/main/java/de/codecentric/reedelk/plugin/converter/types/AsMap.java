package de.codecentric.reedelk.plugin.converter.types;

import de.codecentric.reedelk.plugin.converter.ValueConverter;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class AsMap implements ValueConverter<Map<String,Object>> {

    @Override
    public String toText(Object value) {
        // This method is not used because in the table we
        // are using to display map values we are doing
        // a custom mapping between the Map and the Vector
        // model underlying the table.
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<String, Object> from(String value) {
        try {
            JSONObject jsonObject = new JSONObject(value);
            return asMap(jsonObject);
        } catch (Exception exception) {
            return new HashMap<>();
        }
    }

    @Override
    public Map<String, Object> from(String propertyName, JSONObject object) {
        if (object.has(propertyName) && !object.isNull(propertyName)) {
            JSONObject jsonObject = object.getJSONObject(propertyName);
            return asMap(jsonObject);
        } else {
            return new HashMap<>();
        }
    }

    private Map<String,Object> asMap(JSONObject jsonObject) {
        Map<String, Object> map = new LinkedHashMap<>();
        jsonObject.keySet().forEach(key -> map.put(key, jsonObject.get(key)));
        return map;
    }
}
