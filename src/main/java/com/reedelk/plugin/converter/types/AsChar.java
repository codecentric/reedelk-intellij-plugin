package com.reedelk.plugin.converter.types;

import com.reedelk.plugin.converter.ValueConverter;
import com.reedelk.runtime.converter.RuntimeConverters;
import com.reedelk.runtime.converter.json.JsonObjectConverter;
import org.json.JSONObject;

public class AsChar implements ValueConverter<String> {

    @Override
    public String toText(Object value) {
        return RuntimeConverters.getInstance().convert(value, String.class);
    }

    @Override
    public String from(String value) {
        return value;
    }

    @Override
    public String from(String propertyName, JSONObject object) {
        return JsonObjectConverter.getInstance().convert(String.class, object, propertyName);
    }
}
