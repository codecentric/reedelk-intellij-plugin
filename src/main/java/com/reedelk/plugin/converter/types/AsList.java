package com.reedelk.plugin.converter.types;

import com.reedelk.plugin.converter.ValueConverter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AsList implements ValueConverter<List<Object>> {

    @Override
    public String toText(Object value) {
        // This method is not used because in the List we
        // we are doing a custom mapping between the List and
        // the data model underlying the table.
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Object> from(String value) {
        try {
            JSONArray jsonArray = new JSONArray(value);
            return jsonArray.toList();
        } catch (Exception exception) {
            return new ArrayList<>();
        }
    }

    @Override
    public List<Object> from(String propertyName, JSONObject object) {
        if (object.has(propertyName) && !object.isNull(propertyName)) {
            JSONArray jsonArray = object.getJSONArray(propertyName);
            return jsonArray.toList();
        } else {
            return new ArrayList<>();
        }
    }
}
