package com.reedelk.plugin.converter.types;

import com.reedelk.plugin.converter.ValueConverter;
import com.reedelk.runtime.converter.RuntimeConverters;
import com.reedelk.runtime.converter.json.JsonObjectConverter;
import org.json.JSONObject;

public class AsBooleanObject implements ValueConverter<Boolean> {

    @Override
    public String toText(Object value) {
        return RuntimeConverters.getInstance().convert(value, String.class);
    }

    @Override
    public Boolean from(String value) {
        return RuntimeConverters.getInstance().convert(value, Boolean.class);
    }

    @Override
    public Boolean from(String propertyName, JSONObject object) {
        return JsonObjectConverter.getInstance().convert(Boolean.class, object, propertyName);
    }
}
