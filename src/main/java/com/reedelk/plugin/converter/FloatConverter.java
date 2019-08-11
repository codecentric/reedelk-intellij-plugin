package com.reedelk.plugin.converter;

import com.google.common.base.Defaults;
import org.json.JSONObject;

public class FloatConverter implements ValueConverter<Float> {

    @Override
    public String toText(Object value) {
        Float realValue = (Float) value;
        return realValue == null ?
                null :
                Float.toString(realValue);
    }

    @Override
    public Float from(String value) {
        try {
            return Float.parseFloat(value);
        } catch (NumberFormatException e) {
            return Defaults.defaultValue(Float.class);
        }
    }

    @Override
    public Float from(String propertyName, JSONObject object) {
        return object.isNull(propertyName) ?
                null :
                object.getFloat(propertyName);
    }
}
