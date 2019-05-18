package com.esb.plugin.converter;

import org.json.JSONObject;

import java.math.BigDecimal;

public class BigDecimalConverter implements ValueConverter<BigDecimal> {

    @Override
    public String toText(Object value) {
        BigDecimal realValue = (BigDecimal) value;
        return realValue == null ?
                null :
                realValue.toPlainString();
    }

    @Override
    public BigDecimal from(String value) {
        try {
            return new BigDecimal(value);
        } catch (NumberFormatException e) {
            // Default value for BigDecimal is null
            return null;
        }
    }

    @Override
    public BigDecimal from(String propertyName, JSONObject object) {
        return object.isNull(propertyName) ?
                null :
                object.getBigDecimal(propertyName);
    }

}
