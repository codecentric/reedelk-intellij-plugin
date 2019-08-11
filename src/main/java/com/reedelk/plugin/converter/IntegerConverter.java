package com.reedelk.plugin.converter;

import com.google.common.base.Defaults;
import org.json.JSONObject;

public class IntegerConverter implements ValueConverter<Integer> {

    @Override
    public String toText(Object value) {
        Integer realValue = (Integer) value;
        return realValue == null ?
                null :
                Integer.toString(realValue);
    }

    @Override
    public Integer from(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return Defaults.defaultValue(Integer.class);
        }
    }

    @Override
    public Integer from(String propertyName, JSONObject object) {
        return object.isNull(propertyName) ?
                null :
                object.getInt(propertyName);
    }

}
