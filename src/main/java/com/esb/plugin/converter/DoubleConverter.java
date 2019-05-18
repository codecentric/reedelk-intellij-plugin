package com.esb.plugin.converter;

import com.google.common.base.Defaults;
import org.json.JSONObject;

public class DoubleConverter implements ValueConverter<Double> {

    @Override
    public String toText(Object value) {
        Double realValue = (Double) value;
        return realValue == null ?
                null :
                Double.toString(realValue);
    }

    @Override
    public Double from(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return Defaults.defaultValue(Double.class);
        }
    }

    @Override
    public Double from(String propertyName, JSONObject object) {
        return object.isNull(propertyName) ?
                null :
                object.getDouble(propertyName);
    }

}
