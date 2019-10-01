package com.reedelk.plugin.converter;

import com.reedelk.runtime.api.commons.ScriptUtils;
import org.json.JSONObject;

abstract class AbstractDynamicConverter<T> implements ValueConverter<Object> {

    @Override
    public String toText(Object value) {
        return value instanceof String ?
                (String) value : delegate().toText(value);
    }

    @Override
    public Object from(String value) {
        return ScriptUtils.isScript(value) ?
                value : delegate().from(value);
    }

    @Override
    public Object from(String propertyName, JSONObject object) {
        if (object.isNull(propertyName)) {
            return null;
        } else {
            Object value = object.get(propertyName);
            return ScriptUtils.isScript(value) ?
                    value : delegate().from(propertyName, object);
        }
    }

    protected abstract ValueConverter<T> delegate();

}
