package com.reedelk.plugin.converter;

import com.reedelk.runtime.api.commons.ScriptUtils;
import org.json.JSONObject;

public class DynamicIntegerConverter implements ValueConverter<Object> {

    private IntegerConverter delegate = new IntegerConverter();

    @Override
    public String toText(Object value) {
        if (value instanceof String) {
            return (String) value;
        } else {
            return delegate.toText(value);
        }
    }

    @Override
    public Object from(String value) {
        if (ScriptUtils.isScript(value)) {
            return value;
        } else {
            return delegate.from(value);
        }
    }

    @Override
    public Object from(String propertyName, JSONObject object) {
        if (object.isNull(propertyName)) {
            return null;
        } else {
            Object value = object.get(propertyName);
            if (ScriptUtils.isScript(value)) {
                return value;
            } else {
                return object.getInt(propertyName);
            }
        }
    }
}
